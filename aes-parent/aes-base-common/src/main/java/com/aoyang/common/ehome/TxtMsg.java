package com.aoyang.common.ehome;

import java.io.Serializable;
import java.util.List;

/**
 * @Author went
 * @Date 2020/4/2 10:12
 * @Version 1.0
 */
public class TxtMsg implements Serializable {

    private List<String> userIds;

    // 文本内容，可以通过\n等方式换行
    private String context;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
