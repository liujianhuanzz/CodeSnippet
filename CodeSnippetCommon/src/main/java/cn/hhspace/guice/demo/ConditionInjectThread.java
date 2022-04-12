package cn.hhspace.guice.demo;

import cn.hhspace.guice.demo.annotations.DbImplement;
import cn.hhspace.guice.modules.DbModule2;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.util.Properties;
import java.util.Set;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/12 3:57 下午
 * @Descriptions: 当前运行不起来，还差研究一下怎么写
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
        Properties properties = baseInjector.getInstance(Properties.class);
        Module dbModule = new DbModule2(properties);
        baseInjector.injectMembers(dbModule);

        Injector injector = Guice.createInjector(dbModule);

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
