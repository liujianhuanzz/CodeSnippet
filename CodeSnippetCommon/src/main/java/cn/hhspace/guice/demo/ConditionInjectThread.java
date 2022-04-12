package cn.hhspace.guice.demo;

import cn.hhspace.guice.modules.DbModule2;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.util.Properties;

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

    @Inject
    DbModuleTest dbModuleTest;

    @Override
    public void run() {
        Properties properties = baseInjector.getInstance(Properties.class);
        Module dbModule = new DbModule2(properties);
        baseInjector.injectMembers(dbModule);

        Guice.createInjector(dbModule);
        dbModuleTest.getDb().connectTest();
    }

    static class DbModuleTest {
        Db db;
        @Inject
        public DbModuleTest(Db db) {
            this.db = db;
        }

        public Db getDb() {
            return db;
        }
    }
}
