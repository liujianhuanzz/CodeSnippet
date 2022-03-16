package cn.hhspace.guice.lifecycle;

import cn.hhspace.guice.lifecycle.annotations.LifecycleStart;
import cn.hhspace.guice.lifecycle.annotations.LifecycleStop;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:19 下午
 * @Descriptions:
 */
@Slf4j
public class Lifecycle {

    public enum Stage {
        INIT,
        NORMAL,
        SERVER,
        ANNOUNCEMENTS
    }

    public enum State {
        NOT_STARTED,
        RUNNING,
        STOP
    }

    private final NavigableMap<Stage, CopyOnWriteArrayList<Handler>> handlers;
    /** This lock is used to linearize all calls to Handler.start() and Handler.stop() on the managed handlers. */
    private final Lock startStopLock = new ReentrantLock();
    private final AtomicReference<State> state = new AtomicReference<>(State.NOT_STARTED);
    private Stage currStage = null;
    private final AtomicBoolean shutdownHookRegistered = new AtomicBoolean(false);
    private final String name;

    public Lifecycle()
    {
        this("anonymous");
    }

    public Lifecycle(String name)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Lifecycle name must not be null or empty");
        this.name = name;
        handlers = new TreeMap<>();
        for (Stage stage : Stage.values()) {
            handlers.put(stage, new CopyOnWriteArrayList<>());
        }
    }

    public <T> T addManagedInstance(T o)
    {
        addHandler(new AnnotationBasedHandler(o));
        return o;
    }

    public <T> T addManagedInstance(T o, Stage stage)
    {
        addHandler(new AnnotationBasedHandler(o), stage);
        return o;
    }

    public <T> T addStartCloseInstance(T o)
    {
        addHandler(new StartCloseHandler(o));
        return o;
    }

    public <T> T addStartCloseInstance(T o, Stage stage)
    {
        addHandler(new StartCloseHandler(o), stage);
        return o;
    }


    public void addHandler(Handler handler)
    {
        addHandler(handler, Stage.NORMAL);
    }

    public void addHandler(Handler handler, Stage stage)
    {
        if (!startStopLock.tryLock()) {
            throw new ISE("Cannot add a handler in the process of Lifecycle starting or stopping");
        }
        try {
            if (!state.get().equals(State.NOT_STARTED)) {
                throw new ISE("Cannot add a handler after the Lifecycle has started, it doesn't work that way.");
            }
            handlers.get(stage).add(handler);
        }
        finally {
            startStopLock.unlock();
        }
    }

    public <T> T addMaybeStartManagedInstance(T o) throws Exception
    {
        addMaybeStartHandler(new AnnotationBasedHandler(o));
        return o;
    }

    public <T> T addMaybeStartManagedInstance(T o, Stage stage) throws Exception
    {
        addMaybeStartHandler(new AnnotationBasedHandler(o), stage);
        return o;
    }

    public <T> T addMaybeStartStartCloseInstance(T o) throws Exception
    {
        addMaybeStartHandler(new StartCloseHandler(o));
        return o;
    }

    public <T> T addMaybeStartStartCloseInstance(T o, Stage stage) throws Exception
    {
        addMaybeStartHandler(new StartCloseHandler(o), stage);
        return o;
    }

    public <T extends Closeable> T addCloseableInstance(T o)
    {
        addHandler(new CloseableHandler(o));
        return o;
    }

    public void addMaybeStartHandler(Handler handler) throws Exception
    {
        addMaybeStartHandler(handler, Stage.NORMAL);
    }

    public void addMaybeStartHandler(Handler handler, Stage stage) throws Exception
    {
        if (!startStopLock.tryLock()) {
            // (*) This check is why the state should be changed before startStopLock.lock() in stop(). This check allows to
            // spot wrong use of Lifecycle instead of entering deadlock, like https://github.com/apache/incubator-druid/issues/3579.
            if (state.get().equals(State.STOP)) {
                throw new ISE("Cannot add a handler in the process of Lifecycle stopping");
            }
            startStopLock.lock();
        }
        try {
            if (state.get().equals(State.STOP)) {
                throw new ISE("Cannot add a handler after the Lifecycle has stopped");
            }
            if (state.get().equals(State.RUNNING)) {
                if (stage.compareTo(currStage) <= 0) {
                    handler.start();
                }
            }
            handlers.get(stage).add(handler);
        }
        finally {
            startStopLock.unlock();
        }
    }

    public void start() throws Exception
    {
        startStopLock.lock();
        try {
            if (!state.get().equals(State.NOT_STARTED)) {
                throw new ISE("Already started");
            }
            if (!state.compareAndSet(State.NOT_STARTED, State.RUNNING)) {
                throw new ISE("stop() is called concurrently with start()");
            }
            for (Map.Entry<Stage, ? extends List<Handler>> e : handlers.entrySet()) {
                currStage = e.getKey();
                log.info("Starting lifecycle [{}] stage [{}]", name, currStage.name());
                for (Handler handler : e.getValue()) {
                    handler.start();
                }
            }
            log.info("Successfully started lifecycle [{}]", name);
        }
        finally {
            startStopLock.unlock();
        }
    }

    public void stop()
    {
        // This CAS outside of a block guarded by startStopLock is the only reason why state is AtomicReference rather than
        // a simple variable. State change before startStopLock.lock() is needed for the new state visibility during the
        // check in addMaybeStartHandler() marked by (*).
        if (!state.compareAndSet(State.RUNNING, State.STOP)) {
            log.info("Lifecycle [{}] already stopped and stop was called. Silently skipping", name);
            return;
        }
        startStopLock.lock();
        try {
            RuntimeException thrown = null;

            for (Stage s : handlers.navigableKeySet().descendingSet()) {
                log.info("Stopping lifecycle [{}] stage [{}]", name, s.name());
                for (Handler handler : Lists.reverse(handlers.get(s))) {
                    try {
                        handler.stop();
                    }
                    catch (RuntimeException e) {
                        log.warn("Lifecycle [{}] encountered exception while stopping {}", name, handler, e);
                        if (thrown == null) {
                            thrown = e;
                        }
                    }
                }
            }

            if (thrown != null) {
                throw thrown;
            }
        }
        finally {
            startStopLock.unlock();
        }
    }

    public void ensureShutdownHook()
    {
        if (shutdownHookRegistered.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(
                    new Thread(
                            new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    log.info("Lifecycle [{}] running shutdown hook", name);
                                    stop();
                                }
                            }
                    )
            );
        }
    }

    public void join() throws InterruptedException
    {
        ensureShutdownHook();
        Thread.currentThread().join();
    }

    public interface Handler
    {
        void start() throws Exception;

        void stop();
    }

    @Slf4j
    private static class AnnotationBasedHandler implements Handler
    {
        private final Object o;

        public AnnotationBasedHandler(Object o)
        {
            this.o = o;
        }

        @Override
        public void start() throws Exception
        {
            for (Method method : o.getClass().getMethods()) {
                boolean doStart = false;
                for (Annotation annotation : method.getAnnotations()) {
                    if (LifecycleStart.class.getName().equals(annotation.annotationType().getName())) {
                        doStart = true;
                        break;
                    }
                }
                if (doStart) {
                    log.info("Invoking start method[{}] on object[{}].", method, o);
                    method.invoke(o);
                }
            }
        }

        @Override
        public void stop()
        {
            for (Method method : o.getClass().getMethods()) {
                boolean doStop = false;
                for (Annotation annotation : method.getAnnotations()) {
                    if (LifecycleStop.class.getName().equals(annotation.annotationType().getName())) {
                        doStop = true;
                        break;
                    }
                }
                if (doStop) {
                    log.info("Invoking stop method[{}] on object[{}].", method, o);
                    try {
                        method.invoke(o);
                    }
                    catch (Exception e) {
                        log.error("Exception when stopping method[{}] on object[{}]", method, o, e);
                    }
                }
            }
        }
    }

    @Slf4j
    private static class StartCloseHandler implements Handler
    {
        private final Object o;
        private final Method startMethod;
        private final Method stopMethod;

        public StartCloseHandler(Object o)
        {
            this.o = o;
            try {
                startMethod = o.getClass().getMethod("start");
                stopMethod = o.getClass().getMethod("close");
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public void start() throws Exception
        {
            log.info("Starting object[{}]", o);
            startMethod.invoke(o);
        }

        @Override
        public void stop()
        {
            log.info("Stopping object[{}]", o);
            try {
                stopMethod.invoke(o);
            }
            catch (Exception e) {
                log.error("Unable to invoke stopMethod() on {}", o.getClass(), e);
            }
        }
    }

    @Slf4j
    private static class CloseableHandler implements Handler
    {
        private final Closeable o;

        private CloseableHandler(Closeable o)
        {
            this.o = o;
        }

        @Override
        public void start()
        {
            // do nothing
        }

        @Override
        public void stop()
        {
            log.info("Closing object[{}]", o);
            try {
                o.close();
            }
            catch (Exception e) {
                log.error("Exception when closing object [{}]", o, e);
            }
        }
    }
}
