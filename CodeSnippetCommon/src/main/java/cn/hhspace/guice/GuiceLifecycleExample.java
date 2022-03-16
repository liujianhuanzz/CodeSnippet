package cn.hhspace.guice;

import cn.hhspace.guice.lifecycle.Lifecycle;
import cn.hhspace.guice.lifecycle.LifecycleModule;
import cn.hhspace.guice.lifecycle.annotations.LifecycleStart;
import cn.hhspace.guice.lifecycle.annotations.LifecycleStop;
import cn.hhspace.guice.lifecycle.annotations.ManageLifecycle;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:35 下午
 * @Descriptions: Guice生命周期管理
 */
public class GuiceLifecycleExample {

    public static void main( String[] args ) throws Exception {
        final Injector injector = Guice.createInjector(new LifecycleModule());

        final Bootstrap bootstrap = injector.getInstance(Bootstrap.class);
        bootstrap.run();
    }

    public static class Bootstrap{
        // 必须主动注入ManageLifecycle，否则需要通过Lifecycle.addHandler主动注册
        private AnnotationLifecycle annotationLifecycle;
        private Lifecycle lifecycle;
        @Inject
        public Bootstrap(Lifecycle lifecycle, AnnotationLifecycle annotationLifecycle) {
            this.lifecycle = lifecycle;
            this.annotationLifecycle = annotationLifecycle;
        }

        public void run() throws Exception {
            System.out.println("Bootstrap run");

            lifecycle.addHandler(new CodeAddLifecycle(), Lifecycle.Stage.ANNOUNCEMENTS);
            lifecycle.addHandler(new Lifecycle.Handler() {
                @Override
                public void start() throws Exception {
                    System.out.println("AnonymousHandlerLifecycle start");
                }

                @Override
                public void stop() {
                    System.out.println("AnonymousHandlerLifecycle stop");
                }
            }, Lifecycle.Stage.SERVER);

            lifecycle.start();
            lifecycle.join();
        }
    }

    @ManageLifecycle
    public static class AnnotationLifecycle {
        @LifecycleStart
        public void start() {
            System.out.println("AnnotationLifecycle start");
        }

        @LifecycleStop
        public void stop()
        {
            System.out.println("AnnotationLifecycle stop");
        }
    }

    public static class CodeAddLifecycle implements Lifecycle.Handler {

        @Override
        public void start() throws Exception {
            System.out.println("CodeAddLifecycle start");
        }

        @Override
        public void stop() {
            System.out.println("CodeAddLifecycle stop");
        }
    }
}
