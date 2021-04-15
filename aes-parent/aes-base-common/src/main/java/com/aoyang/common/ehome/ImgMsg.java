package com.aoyang.common.ehome;

import java.io.Serializable;
import java.util.List;

/**
 *@author chenyijie
 *@Date  2020/6/22 2:34 下午
 */
public class ImgMsg implements Serializable {

    private List<String> userIds;

    private List<Article> articleList;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<Article> getArticlesList() {
        return articleList;
    }

    public void setArticlesList(List<Article> articleList) {
        this.articleList = articleList;
    }
}
