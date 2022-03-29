package cn.hhspace.flink.udf;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/29 10:44 上午
 * @Descriptions: 反射工具
 */
public class ReflectionUtils {
    private static final Class<?>[] EMPTY_ARRAY = new Class[0];
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap();

    /**
     * Create an object for the given className
     *
     * @param className class of which an object is created
     * @return a new object
     */
    public static <T> T newInstance(String className) {
        Class<?> theClass = null;
        try {
            theClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return (T) newInstance(theClass);
    }

    /**
     * Create an object for the given class
     *
     * @param theClass class of which an object is created
     * @return a new object
     */
    public static <T> T newInstance(Class<T> theClass) {
        T result;
        try {
            Constructor<T> meth = (Constructor) CONSTRUCTOR_CACHE.get(theClass);
            if (meth == null) {
                meth = theClass.getDeclaredConstructor(EMPTY_ARRAY);
                meth.setAccessible(true);
                CONSTRUCTOR_CACHE.put(theClass, meth);
            }

            result = meth.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}

