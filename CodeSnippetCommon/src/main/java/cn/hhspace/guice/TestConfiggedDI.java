package cn.hhspace.guice;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.util.Arrays;
import java.util.Collection;

public class TestConfiggedDI {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(makeDefualtModules());
        Db instance = injector.getInstance(Db.class);
        instance.connectTest();
        instance.doQuery();
    }

    private static Collection<Module> makeDefualtModules() {
        return ImmutableList.of(
                new PropertiesModule(Arrays.asList("runtime.properties")),
                new DbModule()
        );
    }
}
