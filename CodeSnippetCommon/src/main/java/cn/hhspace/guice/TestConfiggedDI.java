package cn.hhspace.guice;

import cn.hhspace.guice.demo.Db;
import cn.hhspace.guice.modules.DbModule;
import cn.hhspace.guice.modules.PropertiesModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.util.Arrays;
import java.util.Collection;

public class TestConfiggedDI {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(makeDefaultModules());
        Db instance = injector.getInstance(Db.class);
        instance.connectTest();
        instance.doQuery();
    }

    private static Collection<Module> makeDefaultModules() {
        return ImmutableList.of(
                new PropertiesModule(Arrays.asList("runtime.properties")),
                new DbModule()
        );
    }
}
