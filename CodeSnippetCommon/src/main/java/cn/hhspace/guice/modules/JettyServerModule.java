package cn.hhspace.guice.modules;

import cn.hhspace.guice.common.RE;
import cn.hhspace.guice.demo.controller.IndexResource;
import cn.hhspace.guice.inialization.ServerConfig;
import cn.hhspace.guice.jetty.JettyServerInitializer;
import cn.hhspace.guice.lifecycle.Lifecycle;
import cn.hhspace.guice.mapbinder.Jerseys;
import cn.hhspace.guice.mapbinder.JsonConfigProvider;
import cn.hhspace.guice.mapbinder.annotations.JSR311Resource;
import cn.hhspace.guice.mapbinder.annotations.LazySingleton;
import cn.hhspace.utils.Logger;
import com.google.common.primitives.Ints;
import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 2:50 下午
 * @Descriptions:
 */
public class JettyServerModule extends JerseyServletModule {
    private static final Logger log = new Logger(JettyServerModule.class);

    @Override
    protected void configureServlets() {
        Binder binder = binder();

        JsonConfigProvider.bind(binder, "druid.server.http", ServerConfig.class);

        binder.bind(GuiceContainer.class).to(MyGuiceContainer.class);
        binder.bind(MyGuiceContainer.class).in(Scopes.SINGLETON);

        serve("/*").with(MyGuiceContainer.class);

        Multibinder.newSetBinder(binder, Handler.class);
    }

    public static class MyGuiceContainer extends GuiceContainer {
        private final Set<Class<?>> resources;

        @Inject
        public MyGuiceContainer(
                Injector injector,
                @JSR311Resource Set<Class<?>> resources
        ) {
            super(injector);
            this.resources = resources;
        }

        @Override
        protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig) throws ServletException {
            return new DefaultResourceConfig(resources);
        }
    }

    @Provides
    @LazySingleton
    public Server getServer(
            final Injector injector,
            final Lifecycle lifecycle,
            final ServerConfig serverConfig
            )
    {
        return makeAndInitializeServer(
                injector,
                lifecycle,
                serverConfig
        );
    }

    private Server makeAndInitializeServer(
            Injector injector,
            Lifecycle lifecycle,
            ServerConfig config
    )
    {
        int numServerThreads = config.getNumThreads();

        final QueuedThreadPool threadPool;
        if (config.getQueueSize() == Integer.MAX_VALUE) {
            threadPool = new QueuedThreadPool();
            threadPool.setMinThreads(numServerThreads);
            threadPool.setMaxThreads(numServerThreads);
        } else {
            threadPool = new QueuedThreadPool(
                    numServerThreads,
                    numServerThreads,
                    60000, // same default is used in other case when threadPool = new QueuedThreadPool()
                    new LinkedBlockingQueue<>(config.getQueueSize())
            );
        }
        threadPool.setDaemon(true);

        final Server server = new Server(threadPool);

        server.addBean(new ScheduledExecutorScheduler("JettyScheduler", true), true);

        final List<ServerConnector> serverConnectors = new ArrayList<>();

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setRequestHeaderSize(config.getMaxRequestHeaderSize());
        httpConfiguration.setSendServerVersion(false);

        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
        connector.setHost("127.0.0.1");
        connector.setPort(8092);

        serverConnectors.add(connector);

        final ServerConnector[] connectors = new ServerConnector[serverConnectors.size()];
        int index = 0;
        for (ServerConnector conn : serverConnectors) {
            connectors[index++] = conn;
            conn.setIdleTimeout(Ints.checkedCast(config.getMaxIdleTime().toStandardDuration().getMillis()));
        }

        server.setConnectors(connectors);
        final long gracefulStop = config.getGracefulShutdownTimeout().toStandardDuration().getMillis();
        if (gracefulStop > 0) {
            server.setStopTimeout(gracefulStop);
        }
        server.addLifeCycleListener(new LifeCycle.Listener()
        {
            @Override
            public void lifeCycleStarting(LifeCycle event)
            {
                log.debug("Jetty lifecycle starting [%s]", event.getClass());
            }

            @Override
            public void lifeCycleStarted(LifeCycle event)
            {
                log.debug("Jetty lifeycle started [%s]", event.getClass());
            }

            @Override
            public void lifeCycleFailure(LifeCycle event, Throwable cause)
            {
                log.error(cause, "Jetty lifecycle event failed [%s]", event.getClass());
            }

            @Override
            public void lifeCycleStopping(LifeCycle event)
            {
                log.debug("Jetty lifecycle stopping [%s]", event.getClass());
            }

            @Override
            public void lifeCycleStopped(LifeCycle event)
            {
                log.debug("Jetty lifecycle stopped [%s]", event.getClass());
            }
        });

        //initialize server
        JettyServerInitializer initializer = injector.getInstance(JettyServerInitializer.class);
        initializer.initialize(server, injector);

        lifecycle.addHandler(
                new Lifecycle.Handler() {
                    @Override
                    public void start() throws Exception {
                        log.debug("Starting Jetty Server...");
                        server.start();
                    }

                    @Override
                    public void stop() {
                        try {
                            final long unannounceDelay = config.getUnannouncePropagationDelay().toStandardDuration().getMillis();
                            if (unannounceDelay > 0) {
                                log.info("Sleeping %s ms for unannouncement to propagate.", unannounceDelay);
                                Thread.sleep(unannounceDelay);
                            } else {
                                log.debug("Skipping unannounce wait.");
                            }
                            log.debug("Stopping Jetty Server...");
                            server.stop();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RE(e, "Interrupted waiting for jetty shutdown.");
                        } catch (Exception e) {
                            log.warn(e, "Unable to stop Jetty server.");
                        }
                    }
                },
                Lifecycle.Stage.SERVER
        );

        return server;
    }
}
