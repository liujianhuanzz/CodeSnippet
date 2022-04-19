package cn.hhspace.guice.demo;

import cn.hhspace.guice.demo.annotations.DbImplement;
import cn.hhspace.guice.mapbinder.JsonConfigProvider;
import cn.hhspace.guice.modules.BaseModules;
import cn.hhspace.guice.modules.DbModule2;
import cn.hhspace.guice.modules.SecondaryModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.*;
import com.google.inject.util.Modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/12 3:57 下午
 * @Descriptions: 已经调通了
 */
public class ConditionInjectThread implements Runnable{

    Injector baseInjector;

    @Inject
    public void configure(Injector injector)
    {
        this.baseInjector = injector;
    }

    @Override
    public void run() {

        List<Module> modules = new ArrayList<>();

        baseInjector.injectMembers(SecondaryModule.class);
        modules.add(baseInjector.getInstance(SecondaryModule.class));
        modules.add(new DbModule2(baseInjector.getInstance(Properties.class)));

        Injector injector = Guice.createInjector(modules);

        Properties props = injector.getInstance(Properties.class);
        System.out.println(props.getProperty("db.type"));

        for (Db db : injector.getInstance(DbModuleTest.class).getDbs()) {
            db.connectTest();
        }
    }

    static class DbModuleTest {
        @Inject
        @DbImplement
        Set<Db> dbs;

        public DbModuleTest() {}

        public Set<Db> getDbs() {
            return dbs;
        }
    }
}
