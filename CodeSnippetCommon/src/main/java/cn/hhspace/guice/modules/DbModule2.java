package cn.hhspace.guice.modules;

import cn.hhspace.guice.demo.Db;
import cn.hhspace.guice.demo.MySQLDb;
import cn.hhspace.guice.demo.PostGreSQLDb;
import cn.hhspace.guice.demo.PostGreSQLDbConfig;
import cn.hhspace.guice.demo.annotations.DbImplement;
import cn.hhspace.guice.mapbinder.ConditionalMultibind;
import cn.hhspace.guice.mapbinder.JsonConfigProvider;
import com.google.common.base.Predicates;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;

import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/12 5:00 下午
 * @Descriptions:
 */
public class DbModule2 implements Module {

    private Properties properties;

    public DbModule2(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void configure(Binder binder) {
        ConditionalMultibind<Db> multibind = ConditionalMultibind.create(properties, binder, Db.class, DbImplement.class);
        multibind.addConditionBinding("db.type", Predicates.equalTo("mysql"), MySQLDb.class);
        multibind.addConditionBinding("db.type", Predicates.equalTo("postgresql"), PostGreSQLDb.class);

        JsonConfigProvider.bind(binder, "db.type.postgresql", PostGreSQLDbConfig.class);
    }
}
