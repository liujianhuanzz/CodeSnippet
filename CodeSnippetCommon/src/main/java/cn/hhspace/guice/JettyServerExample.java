package cn.hhspace.guice;

import cn.hhspace.guice.demo.JettyServerThread;
import cn.hhspace.guice.modules.BaseModules;
import cn.hhspace.guice.modules.PropertiesModule;
import cn.hhspace.guice.modules.SecondaryModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.util.Arrays;
import java.util.Collection;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 2:31 下午
 * @Descriptions: Jetty Server With Guice
 */
public class JettyServerExample {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(makeDefaultModules());
        JettyServerThread jettyServerThread = new JettyServerThread();
        injector.injectMembers(jettyServerThread);
        jettyServerThread.run();
    }

    private static Collection<Module> makeDefaultModules() {
        return ImmutableList.of(
                new BaseModules(),
                new PropertiesModule(Arrays.asList("runtime.properties")),
                binder -> {
                    binder.bind(SecondaryModule.class);
                }
        );
    }
}
