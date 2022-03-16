package cn.hhspace.guice.lifecycle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:09 下午
 * @Descriptions: 生命周期结束注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LifecycleStop {
}
