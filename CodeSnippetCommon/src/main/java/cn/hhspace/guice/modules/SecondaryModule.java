package cn.hhspace.guice.modules;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;

import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/18 6:38 下午
 * @Descriptions:
 */
public class SecondaryModule implements Module {

    Properties properties;

    @Inject
    public SecondaryModule(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void configure(Binder binder) {
        binder.install(new BaseModules());
        binder.bind(Properties.class).toInstance(properties);
    }
}
