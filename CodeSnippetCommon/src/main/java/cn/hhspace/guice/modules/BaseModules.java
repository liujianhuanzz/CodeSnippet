package cn.hhspace.guice.modules;

import cn.hhspace.guice.mapbinder.JsonConfigurator;
import cn.hhspace.guice.mapbinder.annotations.LazySingleton;
import com.google.inject.*;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/8 6:42 下午
 * @Descriptions:
 */
public class BaseModules implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bindScope(LazySingleton.class, new Scope() {
            @Override
            public <T> Provider<T> scope(Key<T> key, Provider<T> provider) {
                return Scopes.SINGLETON.scope(key, provider);
            }
        });

        binder.bind(Validator.class).toInstance(Validation.buildDefaultValidatorFactory().getValidator());
        binder.bind(JsonConfigurator.class).in(LazySingleton.class);
    }
}
