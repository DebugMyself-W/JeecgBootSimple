package com.aoyang.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.aoyang.common.EhomeUserIdHolder;
import com.aoyang.common.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class EhomeTokenInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EhomeTokenInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            LOGGER.info("token为空");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(401);
            JSONObject res = new JSONObject();
            res.put("success", false);
            res.put("msg", "token is empty");
            response.getWriter().append(res.toString());
            return false;
        }
        try {
            String userId = JwtUtil.checkToken(token);
            EhomeUserIdHolder.setUserId(userId);
        } catch (JwtException e) {
            LOGGER.info("token非法或过期");
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JSONObject res = new JSONObject();
            res.put("success", false);
            res.put("msg", "token is expired or illegal");
            response.getWriter().append(res.toString());
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        EhomeUserIdHolder.clear();
    }
}
