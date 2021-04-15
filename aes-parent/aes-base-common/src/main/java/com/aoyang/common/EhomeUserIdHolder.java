package com.aoyang.common;

public final class EhomeUserIdHolder {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    private EhomeUserIdHolder(){};

    /**
     * 获取用户ID
     * @return
     */
    public static String getUserId() {
        return threadLocal.get();
    }

    /**
     * 设置用户ID
     * @param userId
     */
    public static void setUserId(String userId) {
        threadLocal.set(userId);
    }

    /**
     * 清除ThreadLocal
     */
    public static void clear () {
        threadLocal.remove();
    }

}
