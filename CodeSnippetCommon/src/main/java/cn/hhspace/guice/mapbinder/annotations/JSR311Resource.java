package cn.hhspace.guice.mapbinder.annotations;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 3:26 下午
 * @Descriptions:
 */

@BindingAnnotation
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JSR311Resource {
}
