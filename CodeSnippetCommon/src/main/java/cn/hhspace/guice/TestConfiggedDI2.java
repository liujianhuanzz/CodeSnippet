package cn.hhspace.guice;

import cn.hhspace.guice.demo.ConditionInjectThread;
import cn.hhspace.guice.demo.Db;
import cn.hhspace.guice.modules.BaseModules;
import cn.hhspace.guice.modules.DbModule;
import cn.hhspace.guice.modules.PropertiesModule;
import cn.hhspace.guice.modules.SecondaryModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.util.Arrays;
import java.util.Collection;

/**
 * 用来测试条件注入
 */

public class TestConfiggedDI2 {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(makeDefaultModules());
        ConditionInjectThread injectThread = new ConditionInjectThread();
        injector.injectMembers(injectThread);
        injectThread.run();
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
