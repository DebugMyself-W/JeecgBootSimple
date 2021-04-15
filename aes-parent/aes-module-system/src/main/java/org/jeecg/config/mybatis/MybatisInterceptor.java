package org.jeecg.config.mybatis;

import com.aoyang.common.annotation.EndTimeAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

/**
 * mybatis拦截器，自动注入创建人、创建时间、修改人、修改时间
 *
 * @Author scott
 * @Date 2019-01-19
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})/*,
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})*/
}

)
public class MybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String sqlId = mappedStatement.getId();

        log.debug("------sqlId------" + sqlId);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        log.debug("------sqlCommandType------" + sqlCommandType);

        if (parameter == null) {
            return invocation.proceed();
        }
        if (SqlCommandType.INSERT == sqlCommandType) {
            LoginUser sysUser = this.getLoginUser();
            Field[] fields = oConvertUtils.getAllFields(parameter);
            for (Field field : fields) {
                log.debug("------field.name------" + field.getName());
                try {
                    if ("createBy".equals(field.getName())) {
                        field.setAccessible(true);
                        Object local_createBy = field.get(parameter);
                        field.setAccessible(false);
                        if (local_createBy == null || local_createBy.equals("")) {
                            if (sysUser != null) {
                                // 登录人账号
                                field.setAccessible(true);
                                field.set(parameter, sysUser.getUsername());
                                field.setAccessible(false);
                            }
                        }
                    }
                    // 注入创建时间
                    if ("createTime".equals(field.getName())) {
                        field.setAccessible(true);
                        Object local_createDate = field.get(parameter);
                        field.setAccessible(false);
                        if (local_createDate == null || local_createDate.equals("")) {
                            field.setAccessible(true);
                            field.set(parameter, new Date());
                            field.setAccessible(false);
                        }
                    }

                    // 注入start_time
                    if ("startTime".equals(field.getName())) {
                        log.info("拦截器注入start_time");
                        field.setAccessible(true);
                        field.set(parameter, new Date());
                        field.setAccessible(false);

                    }


                    //注入部门编码
                    if ("sysOrgCode".equals(field.getName())) {
                        field.setAccessible(true);
                        Object local_sysOrgCode = field.get(parameter);
                        field.setAccessible(false);
                        if (local_sysOrgCode == null || local_sysOrgCode.equals("")) {
                            // 获取登录用户信息
                            if (sysUser != null) {
                                field.setAccessible(true);
                                field.set(parameter, sysUser.getOrgCode());
                                field.setAccessible(false);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        if (SqlCommandType.UPDATE == sqlCommandType) {
            LoginUser sysUser = this.getLoginUser();
            Field[] fields = null;
            if (parameter instanceof ParamMap) {
                ParamMap<?> p = (ParamMap<?>) parameter;
                //update-begin-author:scott date:20190729 for:批量更新报错issues/IZA3Q--
                if (p.containsKey("et")) {
                    parameter = p.get("et");
                } else {
                    parameter = p.get("param1");
                }
                //update-end-author:scott date:20190729 for:批量更新报错issues/IZA3Q-

                //update-begin-author:scott date:20190729 for:更新指定字段时报错 issues/#516-
                if (parameter == null) {
                    return invocation.proceed();
                }
                //update-end-author:scott date:20190729 for:更新指定字段时报错 issues/#516-

                fields = oConvertUtils.getAllFields(parameter);
            } else {
                fields = oConvertUtils.getAllFields(parameter);
            }

            for (Field field : fields) {
                log.debug("------field.name------" + field.getName());
                try {
                    if ("updateBy".equals(field.getName())) {
                        //获取登录用户信息
                        if (sysUser != null) {
                            // 登录账号
                            field.setAccessible(true);
                            field.set(parameter, sysUser.getUsername());
                            field.setAccessible(false);
                        }
                    }
                    if ("updateTime".equals(field.getName())) {
                        field.setAccessible(true);
                        field.set(parameter, new Date());
                        field.setAccessible(false);
                    }


                    // 注入end_time
                    if ("endTime".equals(field.getName())) {
                        //判断是否存在自定义注解
                        boolean exist = field.isAnnotationPresent(EndTimeAnnotation.class);
                        //如果存在注解,注解required=true则拦截器注入end_time.如果拦截器为false不注入
                        if (exist) {
                            EndTimeAnnotation annotation = field.getAnnotation(EndTimeAnnotation.class);
                            if (annotation.required()) {
                                log.info("注入endTime");
                                field.setAccessible(true);
                                field.set(parameter, new Date());
                                field.setAccessible(false);
                            }
                        } else {
                            field.setAccessible(true);
                            field.set(parameter, new Date());
                            field.setAccessible(false);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        /*if (SqlCommandType.SELECT == sqlCommandType) {

            //注解逻辑判断  添加注解了才拦截
           Class<?> classType = Class.forName(mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf(".")));
            String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1);

            Class<?>  cls  = Class.forName(mName);

            for (Method method : classType.getDeclaredMethods()) {
                if (method.isAnnotationPresent(InterceptAnnotation.class) && mName.equals(method.getName())) {
                    InterceptAnnotation interceptorAnnotation = method.getAnnotation(InterceptAnnotation.class);
                    if (interceptorAnnotation.flag()) {

                        log.info("拦截select方法");
                        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                        String sql = boundSql.getSql();
                        log.info("组装前sql:{}",boundSql.getSql());
                        int index = sql.indexOf("where");
                        StringBuilder str = new StringBuilder(sql);
                        str.insert(index," 1==1 and");

                        //通过反射修改sql
                        Field field = boundSql.getClass().getDeclaredField("sql");
                        field.setAccessible(true);
                        field.set(boundSql, str.toString());
                        log.info("组装完成后的sql:{}",boundSql.getSql());
                        return invocation.proceed();
                    }
                }
            }

        }*/
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub
    }

    //update-begin--Author:scott  Date:20191213 for：关于使用Quzrtz 开启线程任务， #465
    private LoginUser getLoginUser() {
        LoginUser sysUser = null;
        try {
            sysUser = SecurityUtils.getSubject().getPrincipal() != null ? (LoginUser) SecurityUtils.getSubject().getPrincipal() : null;
        } catch (Exception e) {
            //e.printStackTrace();
            sysUser = null;
        }
        return sysUser;
    }
    //update-end--Author:scott  Date:20191213 for：关于使用Quzrtz 开启线程任务， #465

}
