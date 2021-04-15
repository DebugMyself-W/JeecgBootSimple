package com.aoyang.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName RolePass
 * @description: 指定角色code，可以查询接口
 * @author: went
 * @Date 2021/3/26 9:52 上午
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RolePass {

    /**
     * role.code 数组
     */
    String[] value();
}
