package com.hk.annotation;

import com.hk.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyParam {
    boolean required() default false;

    int max()default -1;
    int min()default -1;

    /**
     * 正则
     */
    VerifyRegexEnum regex() default VerifyRegexEnum.NO;
}
