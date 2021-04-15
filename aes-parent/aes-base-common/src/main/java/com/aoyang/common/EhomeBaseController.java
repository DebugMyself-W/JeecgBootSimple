package com.aoyang.common;

import com.aoyang.common.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

public class EhomeBaseController {

    /**
     * ehome 根据token获取当前用户工号（AY055547）
     *  已过时，推荐使用    getEhomeCurrentUserId
     * @param request
     * @return
     */
    @Deprecated
    protected String getCurUserIdByToken(HttpServletRequest request) {
        return JwtUtil.checkToken(request.getHeader("token"));
    }

    /**
     * ehome请求中获取当前用户信息
     * @return
     */
    protected String getEhomeCurrentUserId() {
        return EhomeUserIdHolder.getUserId();
    }
}
