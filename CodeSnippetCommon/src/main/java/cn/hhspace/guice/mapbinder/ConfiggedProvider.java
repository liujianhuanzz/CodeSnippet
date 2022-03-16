package cn.hhspace.guice.mapbinder;


import cn.hhspace.utils.StringUtils;
import com.google.inject.*;
import com.google.inject.util.Types;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Properties;

public class ConfiggedProvider<T> implements Provider<T> {

    private Key<T> key;
    private String property;
    private Key<? extends T> defaultKey;
    private String defaultPropertyValue;

    private Injector injector;
    private Properties props;

    public ConfiggedProvider(String property, Key<T> key, Key<? extends T> defaultKey, String defaultPropertyValue) {
        this.key = key;
        this.property = property;
        this.defaultKey = defaultKey;
        this.defaultPropertyValue = defaultPropertyValue;
    }

    @Inject
    void configure(Injector injector, Properties props) {
        this.injector = injector;
        this.props = props;
    }

    @Override
    public T get() {
        final ParameterizedType mapType = Types.mapOf(
                String.class, Types.newParameterizedType(Provider.class, key.getTypeLiteral().getType())
        );

        final Map<String, Provider<T>> implsMap;
        if (key.getAnnotation() != null) {
            implsMap = (Map<String, Provider<T>>) injector.getInstance(Key.get(mapType, key.getAnnotation()));
        } else if (key.getAnnotationType() != null) {
            implsMap = (Map<String, Provider<T>>) injector.getInstance(Key.get(mapType, key.getAnnotationType()));
        } else {
            implsMap = (Map<String, Provider<T>>) injector.getInstance(Key.get(mapType));
        }

        String implName = props.getProperty(property);
        if (implName == null) {
            if (defaultPropertyValue == null) {
                if (defaultKey == null) {
                    throw new ProvisionException(StringUtils.format("Some value must be configured for [%s]", key));
                }
                return injector.getInstance(defaultKey);
            }
            implName = defaultPropertyValue;
        }
        final Provider<T> provider = implsMap.get(implName);

        if (provider == null) {
            throw new ProvisionException(
                    StringUtils.format("Unknown provider[%s] of %s, known options[%s]", implName, key, implsMap.keySet())
            );
        }

        return provider.get();
    }
}
