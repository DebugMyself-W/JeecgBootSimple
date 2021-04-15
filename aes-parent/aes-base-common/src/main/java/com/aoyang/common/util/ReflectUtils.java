package com.aoyang.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author chenyijie
 * @Date 2020/12/23 8:17 上午
 */
public class ReflectUtils {

    /**
     * 反射修改字段属性注解属性
     */
    public static void changeAnnotationValues(Annotation annotation, Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        //获取 annotation 这个代理实例所持有的 InvocationHandler
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        // 获取 AnnotationInvocationHandler 的 memberValues 字段
        Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
        //允许访问私有变量
        declaredField.setAccessible(true);
        // 获取 required
        Map memberValues = (Map) declaredField.get(invocationHandler);
        // 修改 required 属性值
        map.forEach(memberValues::put);
    }

}
