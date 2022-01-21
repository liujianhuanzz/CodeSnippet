package cn.hhspace.guice;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class DbModule implements Module {
    @Override
    public void configure(Binder binder) {

        ConfiggedProvider<Db> provider = new ConfiggedProvider<>("db.type", Key.get(Db.class), Key.get(MySQLDb.class), null);
        binder.bind(Db.class).toProvider(provider);

        MapBinder<String, Db> mapBinder =
                MapBinder.newMapBinder(binder, TypeLiteral.get(String.class), Key.get(Db.class).getTypeLiteral());
        mapBinder.addBinding("mysql").to(MySQLDb.class);
        mapBinder.addBinding("postgresql").to(PostGreSQLDb.class);
    }
}
