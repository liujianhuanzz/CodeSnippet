package cn.hhspace.guice.modules;

import cn.hhspace.guice.demo.Db;
import cn.hhspace.guice.demo.MySQLDb;
import cn.hhspace.guice.demo.PostGreSQLDb;
import cn.hhspace.guice.demo.PostGreSQLDbConfig;
import cn.hhspace.guice.mapbinder.JsonConfigProvider;
import cn.hhspace.guice.mapbinder.PolyBind;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class DbModule implements Module {

    @Override
    public void configure(Binder binder) {

        PolyBind.createChoice(binder, "db.type", Key.get(Db.class), Key.get(MySQLDb.class));

        MapBinder<String, Db> mapBinder =
                MapBinder.newMapBinder(binder, TypeLiteral.get(String.class), Key.get(Db.class).getTypeLiteral());
        mapBinder.addBinding("mysql").to(MySQLDb.class);
        mapBinder.addBinding("postgresql").to(PostGreSQLDb.class);

        JsonConfigProvider.bind(binder, "db.type.postgresql", PostGreSQLDbConfig.class);
    }
}
