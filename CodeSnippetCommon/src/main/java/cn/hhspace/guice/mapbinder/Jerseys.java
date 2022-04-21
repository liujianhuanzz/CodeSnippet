package cn.hhspace.guice.mapbinder;

import cn.hhspace.guice.mapbinder.annotations.JSR311Resource;
import cn.hhspace.utils.Logger;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 4:34 下午
 * @Descriptions: 绑定接口资源
 */
public class Jerseys {
    private static final Logger log = new Logger(Jerseys.class);

    public static void addResource(Binder binder, Class<?> resourceClazz) {
        log.debug("Adding Jersey resource: " + resourceClazz.getName());
        Multibinder.newSetBinder(binder, new TypeLiteral<Class<?>>(){}, JSR311Resource.class)
                .addBinding()
                .toInstance(resourceClazz);
    }
}
