package com.aoyang.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenyijie
 * @Date 2020/12/28 8:39 上午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Approval {

    String categoryCode() default "";

    //true则发起oa审批
    boolean sendToOa() default false;

    //true则根据oa回传结果进行修改
    boolean receiveFromOa() default false;
}
