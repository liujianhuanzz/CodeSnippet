package cn.hhspace.guice.lifecycle.annotations;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:10 下午
 * @Descriptions:
 * 表示生命周期由Lifecycle管理
 * 注解{@link LifecycleStart} 代表start方法，程序初始化
 * 注解{@link LifecycleStop} 代表stop方法，程序终止前调用
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ScopeAnnotation
public @interface ManageLifecycle {
}
