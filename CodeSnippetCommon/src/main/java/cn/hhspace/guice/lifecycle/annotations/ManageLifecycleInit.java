package cn.hhspace.guice.lifecycle.annotations;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:13 下午
 * @Descriptions:
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ScopeAnnotation
public @interface ManageLifecycleInit {
}
