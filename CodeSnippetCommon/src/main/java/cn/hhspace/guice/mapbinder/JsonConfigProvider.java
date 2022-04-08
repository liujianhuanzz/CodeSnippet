package cn.hhspace.guice.mapbinder;

import cn.hhspace.guice.mapbinder.annotations.LazySingleton;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.util.Types;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/8 4:51 下午
 * @Descriptions:
 */
public class JsonConfigProvider<T> implements Provider<Supplier<T>>
{
    @SuppressWarnings("unchecked")
    public static <T> void bind(Binder binder, String propertyBase, Class<T> classToProvide)
    {
        bind(
                binder,
                propertyBase,
                classToProvide,
                Key.get(classToProvide),
                (Key) Key.get(Types.newParameterizedType(Supplier.class, classToProvide))
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void bindWithDefault(
            Binder binder,
            String propertyBase,
            Class<T> classToProvide,
            Class<? extends T> defaultClass
    )
    {
        bind(
                binder,
                propertyBase,
                classToProvide,
                defaultClass,
                Key.get(classToProvide),
                (Key) Key.get(Types.newParameterizedType(Supplier.class, classToProvide))
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void bind(Binder binder, String propertyBase, Class<T> classToProvide, Annotation annotation)
    {
        bind(
                binder,
                propertyBase,
                classToProvide,
                Key.get(classToProvide, annotation),
                (Key) Key.get(Types.newParameterizedType(Supplier.class, classToProvide), annotation)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void bind(
            Binder binder,
            String propertyBase,
            Class<T> classToProvide,
            Class<? extends Annotation> annotation
    )
    {
        bind(
                binder,
                propertyBase,
                classToProvide,
                Key.get(classToProvide, annotation),
                (Key) Key.get(Types.newParameterizedType(Supplier.class, classToProvide), annotation)
        );
    }

    public static <T> void bind(
            Binder binder,
            String propertyBase,
            Class<T> clazz,
            Key<T> instanceKey,
            Key<Supplier<T>> supplierKey
    )
    {
        binder.bind(supplierKey).toProvider(of(propertyBase, clazz)).in(LazySingleton.class);
        binder.bind(instanceKey).toProvider(new SupplierProvider<>(supplierKey));
    }

    public static <T> void bind(
            Binder binder,
            String propertyBase,
            Class<T> clazz,
            Class<? extends T> defaultClass,
            Key<T> instanceKey,
            Key<Supplier<T>> supplierKey
    )
    {
        binder.bind(supplierKey).toProvider(of(propertyBase, clazz, defaultClass)).in(LazySingleton.class);
        binder.bind(instanceKey).toProvider(new SupplierProvider<>(supplierKey));
    }

    @SuppressWarnings("unchecked")
    public static <T> void bindInstance(
            Binder binder,
            Key<T> bindKey,
            T instance
    )
    {
        binder.bind(bindKey).toInstance(instance);

        final ParameterizedType supType = Types.newParameterizedType(Supplier.class, bindKey.getTypeLiteral().getType());
        final Key supplierKey;

        if (bindKey.getAnnotationType() != null) {
            supplierKey = Key.get(supType, bindKey.getAnnotationType());
        } else if (bindKey.getAnnotation() != null) {
            supplierKey = Key.get(supType, bindKey.getAnnotation());
        } else {
            supplierKey = Key.get(supType);
        }

        binder.bind(supplierKey).toInstance(Suppliers.ofInstance(instance));
    }

    public static <T> JsonConfigProvider<T> of(String propertyBase, Class<T> classToProvide)
    {
        return of(propertyBase, classToProvide, null);
    }

    public static <T> JsonConfigProvider<T> of(
            String propertyBase,
            Class<T> classToProvide,
            Class<? extends T> defaultClass
    )
    {
        return new JsonConfigProvider<>(propertyBase, classToProvide, defaultClass);
    }

    private final String propertyBase;
    private final Class<T> classToProvide;
    private final Class<? extends T> defaultClass;

    private Properties props;
    private JsonConfigurator configurator;

    private Supplier<T> retVal = null;

    public JsonConfigProvider(
            String propertyBase,
            Class<T> classToProvide,
            @Nullable Class<? extends T> defaultClass
    )
    {
        this.propertyBase = propertyBase;
        this.classToProvide = classToProvide;
        this.defaultClass = defaultClass;
    }

    @Inject
    public void inject(
            Properties props,
            JsonConfigurator configurator
    )
    {
        this.props = props;
        this.configurator = configurator;
    }

    @Override
    public Supplier<T> get()
    {
        if (retVal != null) {
            return retVal;
        }

        try {
            final T config = configurator.configurate(props, propertyBase, classToProvide, defaultClass);
            retVal = Suppliers.ofInstance(config);
        }
        catch (RuntimeException e) {
            // When a runtime exception gets thrown out, this provider will get called again if the object is asked for again.
            // This will have the same failed result, 'cause when it's called no parameters will have actually changed.
            // Guice will then report the same error multiple times, which is pretty annoying. Cache a null supplier and
            // return that instead.  This is technically enforcing a singleton, but such is life.
            retVal = Suppliers.ofInstance(null);
            throw e;
        }
        return retVal;
    }
}
