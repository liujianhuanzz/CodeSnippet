package cn.hhspace.guice.mapbinder;

import com.google.common.base.Predicate;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/12 2:20 下午
 * @Descriptions: 条件绑定
 */

/**
 * Provides the ability to conditionally bind an item to a set. The condition is based on the value set in the
 * runtime.properties.
 *
 * Usage example:
 *
 * ConditionalMultibind.create(props, binder, Animal.class)
 *                     .addConditionBinding("animal.type", Predicates.equalTo("cat"), Cat.class)
 *                     .addConditionBinding("animal.type", Predicates.equalTo("dog"), Dog.class);
 *
 * At binding time, this will check the value set for property "animal.type" in props. If the value is "cat", it will
 * add a binding to Cat.class. If the value is "dog", it will add a binding to Dog.class.
 *
 * At injection time, you will get the items that satisfy their corresponding predicates by calling
 * injector.getInstance(Key.get(new TypeLiteral<Set<Animal>>(){}))
 */
public class ConditionalMultibind<T> {
    /**
     * Create a ConditionalMultibind that resolves items to be added to the set at "binding" time.
     *
     * @param properties the runtime properties.
     * @param binder     the binder for the injector that is being configured.
     * @param type       the type that will be injected.
     * @param <T>        interface type.
     *
     * @return An instance of ConditionalMultibind that can be used to add conditional bindings.
     */
    public static <T> ConditionalMultibind<T> create(Properties properties, Binder binder, Class<T> type)
    {
        return new ConditionalMultibind<T>(properties, Multibinder.newSetBinder(binder, type));
    }

    /**
     * Create a ConditionalMultibind that resolves items to be added to the set at "binding" time.
     *
     * @param properties     the runtime properties.
     * @param binder         the binder for the injector that is being configured.
     * @param type           the type that will be injected.
     * @param <T>            interface type.
     * @param annotationType the binding annotation.
     *
     * @return An instance of ConditionalMultibind that can be used to add conditional bindings.
     */
    public static <T> ConditionalMultibind<T> create(
            Properties properties,
            Binder binder,
            Class<T> type,
            Class<? extends Annotation> annotationType
    )
    {
        return new ConditionalMultibind<T>(properties, Multibinder.newSetBinder(binder, type, annotationType));
    }

    /**
     * Create a ConditionalMultibind that resolves items to be added to the set at "binding" time.
     *
     * @param properties the runtime properties.
     * @param binder     the binder for the injector that is being configured.
     * @param type       the type that will be injected.
     * @param <T>        interface type.
     *
     * @return An instance of ConditionalMultibind that can be used to add conditional bindings.
     */
    public static <T> ConditionalMultibind<T> create(Properties properties, Binder binder, TypeLiteral<T> type)
    {
        return new ConditionalMultibind<T>(properties, Multibinder.newSetBinder(binder, type));
    }

    /**
     * Create a ConditionalMultibind that resolves items to be added to the set at "binding" time.
     *
     * @param properties     the runtime properties.
     * @param binder         the binder for the injector that is being configured.
     * @param type           the type that will be injected.
     * @param <T>            interface type.
     * @param annotationType the binding annotation.
     *
     * @return An instance of ConditionalMultibind that can be used to add conditional bindings.
     */
    public static <T> ConditionalMultibind<T> create(
            Properties properties,
            Binder binder,
            TypeLiteral<T> type,
            Class<? extends Annotation> annotationType
    )
    {
        return new ConditionalMultibind<T>(properties, Multibinder.newSetBinder(binder, type, annotationType));
    }


    private final Properties properties;
    private final Multibinder<T> multibinder;

    public ConditionalMultibind(Properties properties, Multibinder<T> multibinder)
    {
        this.properties = properties;
        this.multibinder = multibinder;
    }

    /**
     * Unconditionally bind target to the set.
     *
     * @param target the target class to which it adds a binding.
     *
     * @return self to support a continuous syntax for adding more conditional bindings.
     */
    public ConditionalMultibind<T> addBinding(Class<? extends T> target)
    {
        multibinder.addBinding().to(target);
        return this;
    }

    /**
     * Unconditionally bind target to the set.
     *
     * @param target the target instance to which it adds a binding.
     *
     * @return self to support a continuous syntax for adding more conditional bindings.
     */
    public ConditionalMultibind<T> addBinding(T target)
    {
        multibinder.addBinding().toInstance(target);
        return this;
    }

    /**
     * Unconditionally bind target to the set.
     *
     * @param target the target type to which it adds a binding.
     *
     * @return self to support a continuous syntax for adding more conditional bindings.
     */
    public ConditionalMultibind<T> addBinding(TypeLiteral<T> target)
    {
        multibinder.addBinding().to(target);
        return this;
    }

    /**
     * Conditionally bind target to the set. If "condition" returns true, add a binding to "target".
     *
     * @param property  the property to inspect on
     * @param condition the predicate used to verify whether to add a binding to "target"
     * @param target    the target class to which it adds a binding.
     *
     * @return self to support a continuous syntax for adding more conditional bindings.
     */
    public ConditionalMultibind<T> addConditionBinding(
            String property,
            Predicate<String> condition,
            Class<? extends T> target
    )
    {
        return addConditionBinding(property, null, condition, target);
    }

    public ConditionalMultibind<T> addConditionBinding(
            String property,
            String defaultValue,
            Predicate<String> condition,
            Class<? extends T> target
    )
    {
        if (matchCondition(property, defaultValue, condition)) {
            multibinder.addBinding().to(target);
        }
        return this;
    }

    /**
     * Conditionally bind target to the set. If "condition" returns true, add a binding to "target".
     *
     * @param property  the property to inspect on
     * @param condition the predicate used to verify whether to add a binding to "target"
     * @param target    the target instance to which it adds a binding.
     *
     * @return self to support a continuous syntax for adding more conditional bindings.
     */
    public ConditionalMultibind<T> addConditionBinding(
            String property,
            Predicate<String> condition,
            T target
    )
    {
        if (matchCondition(property, condition)) {
            multibinder.addBinding().toInstance(target);
        }
        return this;
    }

    /**
     * Conditionally bind target to the set. If "condition" returns true, add a binding to "target".
     *
     * @param property  the property to inspect on
     * @param condition the predicate used to verify whether to add a binding to "target"
     * @param target    the target type to which it adds a binding.
     *
     * @return self to support a continuous syntax for adding more conditional bindings.
     */
    public ConditionalMultibind<T> addConditionBinding(
            String property,
            Predicate<String> condition,
            TypeLiteral<T> target
    )
    {
        if (matchCondition(property, condition)) {
            multibinder.addBinding().to(target);
        }
        return this;
    }

    public boolean matchCondition(String property, Predicate<String> condition)
    {
        return matchCondition(property, null, condition);
    }

    public boolean matchCondition(String property, @Nullable String defaultValue, Predicate<String> condition)
    {
        final String value = properties.getProperty(property, defaultValue);
        if (value == null) {
            return false;
        }
        return condition.apply(value);
    }
}
