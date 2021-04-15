package com.aoyang.common.ehome;

import com.alibaba.fastjson.JSON;
import com.aoyang.common.http.HttpClientResult;
import com.aoyang.common.http.HttpClientUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MsgSendHelper {

    private static final String TXT_URL = "/text";

    private static final String IMG_URL = "/news";

    @Value("${ehome.agentId}")
    private String agentId;

    @Value("${ehome.msgApiUrl}")
    private String baseUrl;

    public boolean send(TxtMsg msg) {
        Map<String, Object> param = new HashMap<>();
        param.put("userIds", msg.getUserIds());
        param.put("context", msg.getContext());
        param.put("agentId", agentId);
        try {
            HttpClientResult httpClientResult = HttpClientUtils.postJsonString(baseUrl + TXT_URL, JSON.toJSONString(param));
            if (httpClientResult.getCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            log.error("澳洋e家推送时发生系统异常", e);
        }
        return false;
    }


    public boolean sendImg(ImgMsg imgMsg) {
        Map<String, Object> param = new HashMap<>();
        param.put("userIds", imgMsg.getUserIds());
        param.put("agentId", agentId);
        param.put("articles",imgMsg.getArticlesList());
        try {
            HttpClientResult httpClientResult = HttpClientUtils.postJsonString(baseUrl + IMG_URL, JSON.toJSONString(param));
            if (httpClientResult.getCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            log.error("澳洋e家推送时发生系统异常", e);
        }
        return false;
    }

    /**
     * 推送消息方法封装
     *
     * @param userIds
     * @param title
     * @param url
     * @param context
     * @Date 2020-11-26
     */
    public boolean sendMsg(List<String> userIds, String title, String url, String context) {
        //设置人员
        ImgMsg msg = new ImgMsg();
        msg.setUserIds(userIds);
        //设置内容
        List<Article> articleItem = Lists.newArrayList();
        Article article = new Article();
        article.setTitle(title);
        article.setDescription(context);
        article.setUrl(url);
        articleItem.add(article);
        msg.setArticlesList(articleItem);
        //调用推送信息接口
        return sendImg(msg);
    }
}
