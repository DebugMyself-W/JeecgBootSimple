package com.aoyang.common.http;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Description: httpClient工具类
 *
 * @author went
 * @date Created on 2019年11月5日11:11:53
 */
public class HttpClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

    // 编码格式。发送编码格式统一用UTF-8
    private static final String ENCODING = "UTF-8";

    // 设置连接超时时间，单位毫秒。
    private static final int CONNECT_TIMEOUT = 6000;

    // 请求获取数据的超时时间(即响应时间)，单位毫秒。
    private static final int SOCKET_TIMEOUT = 6000;

    /**
     * 发送get请求；不带请求头和请求参数
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static HttpClientResult doGet(String url) throws Exception {
        return doGet(url, null, null);
    }

    /**
     * 发送get请求；带请求参数
     *
     * @param url    请求地址
     * @param params 请求参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doGet(String url, Map<String, String> params) throws Exception {
        return doGet(url, null, params);
    }

    /**
     * 发送get请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doGet(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        // 创建httpClient对象
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // 创建访问的地址
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }

            // 创建http对象
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            /**
             * setConnectTimeout：设置连接超时时间，单位毫秒。
             * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
             * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
             * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpGet.setConfig(requestConfig);

            // 设置请求头
            packageHeader(headers, httpGet);

            // 执行请求并获得响应结果
            return getHttpClientResult(httpClient, httpGet);
        }
    }

    /**
     * 发送post请求；不带请求头和请求参数
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPost(String url) throws Exception {
        return doPost(url, null, null);
    }

    /**
     * 发送post请求；带请求参数
     *
     * @param url    请求地址
     * @param params 参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPost(String url, Map<String, Object> params) throws Exception {
        return doPost(url, null, params);
    }

    /**
     * 发送post请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPost(String url, Map<String, String> headers, Map<String, Object> params) throws Exception {
        // 创建httpClient对象
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建http对象
            HttpPost httpPost = new HttpPost(url);
            /**
             * setConnectTimeout：设置连接超时时间，单位毫秒。
             * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
             * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
             * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpPost.setConfig(requestConfig);
            // 设置请求头
            /*httpPost.setHeader("Cookie", "");
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");*/
            packageHeader(headers, httpPost);

            // 封装请求参数
            packageParam(params, httpPost);
            // 执行请求并获得响应结果
            return getHttpClientResult(httpClient, httpPost);
        }
    }

    /**
     * post方法，参数为json字符串
     *
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public static HttpClientResult postJsonString(String url, String json) throws Exception {
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            post.setConfig(requestConfig);
            post.addHeader("Content-type", "application/json;charset=utf-8");
            post.setHeader("Accept", "application/json");
            StringEntity s = new StringEntity(json, Charset.forName(ENCODING));
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            return getHttpClientResult(httpclient, post);
        }
    }

    /**
     * post方法，参数为json字符串
     *
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public static HttpClientResult postJsonStringWithoutAcceptHaeder(String url, String json) throws Exception {
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            post.setConfig(requestConfig);
            post.addHeader("Content-type", "application/json;charset=utf-8");
            StringEntity s = new StringEntity(json, Charset.forName(ENCODING));
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            return getHttpClientResult(httpclient, post);
        }
    }

    /**
     * post方法，参数为xml字符串
     * @param url
     * @param soapXml text/xml;charset=UTF-8
     * @return
     * @throws Exception
     */
    public static HttpClientResult postSoapXml(String url, String soapXml) throws Exception{
        try(CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig =
                    RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
            post.setConfig(requestConfig);
            post.setHeader("Content-Type", "text/xml;charset=UTF-8");
            post.setHeader("SOAPAction", "");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            post.setEntity(data);
            return getHttpClientResult(httpclient, post);
        }
    }

    /**
     *
     * @param url
     * @param soapXml application/soap+xml;charset=UTF-8
     * @return
     * @throws Exception
     */
    public static HttpClientResult postSoap1_2Xml(String url, String soapXml) throws Exception{
        try(CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig =
                    RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
            post.setConfig(requestConfig);
            post.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
            post.setHeader("SOAPAction", "");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            post.setEntity(data);
            return getHttpClientResult(httpclient, post);
        }
    }

    /**
     * 发送put请求；不带请求参数
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPut(String url) throws Exception {
        return doPut(url);
    }

    /**
     * 发送put请求；带请求参数
     *
     * @param url    请求地址
     * @param params 参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPut(String url, Map<String, Object> params) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpPut.setConfig(requestConfig);

            packageParam(params, httpPut);
            return getHttpClientResult(httpClient, httpPut);
        }
    }

    /**
     * 发送delete请求；不带请求参数
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static HttpClientResult doDelete(String url) throws Exception {
        HttpDelete httpDelete = new HttpDelete(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpDelete.setConfig(requestConfig);
            return getHttpClientResult(httpClient, httpDelete);
        }
    }

    /**
     * 发送delete请求；带请求参数
     *
     * @param url    请求地址
     * @param params 参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doDelete(String url, Map<String, Object> params) throws Exception {
        if (params == null) {
            params = new HashMap<String, Object>();
        }

        params.put("_method", "delete");
        return doPost(url, params);
    }

    /**
     * Description: 封装请求头
     *
     * @param params
     * @param httpMethod
     */
    public static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
        // 封装请求头
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Description: 封装请求参数
     *
     * @param params
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    public static void packageParam(Map<String, Object> params, HttpEntityEnclosingRequestBase httpMethod)
            throws UnsupportedEncodingException {
        // 封装请求参数
        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<Map.Entry<String, Object>> entrySet = params.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                if (entry.getValue() instanceof List) {
                    for (Object o : (List) entry.getValue()) {
                        nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(o)));
                    }
                } else {
                    nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
                }
            }

            // 设置到请求的http对象中
            httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
        }
    }

    /**
     * Description: 获得响应结果
     *
     * @param httpClient
     * @param httpMethod
     * @return
     * @throws Exception
     */
    public static HttpClientResult getHttpClientResult(CloseableHttpClient httpClient, HttpRequestBase httpMethod) {
        // 执行请求
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpMethod)) {
            // 获取返回结果
            if (httpResponse != null && httpResponse.getStatusLine() != null) {
                String content = "";
                if (httpResponse.getEntity() != null) {
                    content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
                }
                LOGGER.info("访问http接口，返回值{}", content);
                return new HttpClientResult(httpResponse.getStatusLine().getStatusCode(), content);
            }
        } catch (IOException e) {
            LOGGER.error("调用http接口时发生异常", e);
        }
        return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
