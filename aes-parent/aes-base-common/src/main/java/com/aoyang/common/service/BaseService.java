package com.aoyang.common.service;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author chenyijie
 * @Date 2020/12/31 11:12 上午
 */
public interface BaseService<T> extends IService<T> {


    /**
     * 根据主键id单表更新
     *
     * @param init 初始化entity
     * @param t    oldEntity
     */
    @Transactional(rollbackFor = Exception.class)
    default void complexUpdate(T init, T t) {
        //封存旧的
        String id = (String) ReflectUtil.getFieldValue(t, "id");
        update(init, new QueryWrapper<T>().eq("id", id).isNull("end_time"));


        //保存新的
        ReflectUtil.setFieldValue(t, "id", null);
        save(t);
    }


    /**
     * 根据主键id更新(更新附带额外参数)
     *
     * @param init 初始化entity
     * @param t    oldEntity
     * @param map  更新时需要额外更新的参数
     */
    @Transactional(rollbackFor = Exception.class)
    default void complexUpdateWithParams(T init, T t, Map<String, Object> map) {
        //封存旧的
        String id = (String) ReflectUtil.getFieldValue(t, "id");
        update(init, new QueryWrapper<T>().eq("id", id).isNull("end_time"));


        //保存新的
        ReflectUtil.setFieldValue(t, "id", null);

        //额外赋值
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            ReflectUtil.setFieldValue(t, entry.getKey(), entry.getValue());
        }

        save(t);
    }

}
