package com.aoyang.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *@author chenyijie
 *@Date  2020/12/22 3:16 下午
 * mybatis拦截器执行更新操作默认自动注入end_time
 * required=false时不会自动注入
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndTimeAnnotation {


    /**
     * 默认需要required,即自动注入
     */
    boolean required() default true;
}
