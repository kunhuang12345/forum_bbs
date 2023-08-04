package com.hk.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE}) // 目标对象，可以添加方法、属性、类等元素
@Retention(RetentionPolicy.RUNTIME) // 生命周期
@Documented
@Inherited // 表示子类继承此注解
public @interface GlobalInterceptor {
    /**
     * 是否需要登录
     */
    boolean checkLogin() default false;

    /**
     * 是否需要校验参数
     */
    boolean checkParams() default false;

    /**
     * 校验频次
     */
}
