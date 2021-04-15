package com.aoyang.common.aspect;

import com.aoyang.common.EhomeUserIdHolder;
import com.aoyang.common.annotation.RolePass;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @ClassName RolePassAspect
 * @description: 角色判断切面
 *      @see RolePass
 * @author: went
 * @Date 2021/3/26 10:07 上午
 **/
@Slf4j
@Aspect
@Component
public class RolePassAspect {

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Around("@annotation(rolePass)")
    public Object check(ProceedingJoinPoint joinPoint, RolePass rolePass) throws Throwable {
        String userId = EhomeUserIdHolder.getUserId();
        if (StringUtils.isEmpty(userId)) {
            log.warn("未获取到当前用户信息");
            return null;
        }
        final List<String> roles = sysBaseAPI.getRolesByUsername(userId);
        final String[] value = rolePass.value();
        boolean flag = false;
        for (int i = 0; i < value.length; i++) {
            if (roles.contains(value[i])) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            log.warn("当前用户信息没有权限");
            return null;
        }
        return joinPoint.proceed();
    }
}
