package com.tany.demo.httpUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private static final String CHARSET_UTF8 = "UTF-8";
    public static final int CONNECT_TIMEOUT = 10000;
    public static final int SOCKET_TIMEOUT = 10000;

    /**
     * 使用Get方式获取数据
     *
     * @param url      url
     * @param paramMap 参数列表（如果url中已包含参数则可以为null）
     * @param header   消息头(可以为null)
     * @return
     */
    public static HttpResponseModel sendGet(String url, Map<String, String> paramMap, Map<String, String> header) {
        HttpResponseModel responseModel = new HttpResponseModel();
        url = url.trim();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            if (null != paramMap && !paramMap.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>(paramMap.size());
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(list, CHARSET_UTF8));
            }
            logger.info(">>>>>> url:{}", url);

            client = HttpConnectionManager.getHttpClientFromPool(CONNECT_TIMEOUT, SOCKET_TIMEOUT);
            HttpGet httpGet = new HttpGet(url);

            // 设置消息头
            addHeader(httpGet, header);

            response = client.execute(httpGet);
            responseModel = getResult(response);

            httpGet.releaseConnection();
        } catch (Exception e) {
            setErrorCode(responseModel, e);
        } finally {
            close(client, response);
        }

        return responseModel;
    }

    /**
     * POST请求，适合content-type为application/json的接口
     *
     * @param url       请求地址
     * @param jsonParam 请求数据json格式
     * @param header    消息头 没有可为null
     */
    public static HttpResponseModel sendPostJson(String url, String jsonParam, Map<String, String> header) {

        HttpResponseModel responseModel = new HttpResponseModel();
        url = url.trim();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = HttpConnectionManager.getHttpClientFromPool(CONNECT_TIMEOUT, SOCKET_TIMEOUT);
            HttpPost httpPost = new HttpPost(url);

            // 设置消息头
            addHeader(httpPost, header);
            // 设置参数
            httpPost.setEntity(new StringEntity(jsonParam, ContentType.APPLICATION_JSON));

            response = client.execute(httpPost);
            responseModel = getResult(response);

            httpPost.releaseConnection();
        } catch (Exception e) {
            setErrorCode(responseModel, e);
        } finally {
            close(client, response);
        }
        return responseModel;
    }

    /**
     * POST请求，适合content-type为application/x-www-form-urlencoded的接口
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @param header   消息头 没有可为null
     */
    public static HttpResponseModel sendPost(String url, Map<String, String> paramMap, Map<String, String> header) {

        HttpResponseModel responseModel = new HttpResponseModel();
        url = url.trim();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = HttpConnectionManager.getHttpClientFromPool(CONNECT_TIMEOUT, SOCKET_TIMEOUT);
            HttpPost httpPost = new HttpPost(url);

            // 设置参数
            if (null != paramMap && !paramMap.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(list, CHARSET_UTF8));
            }


            // 设置消息头
            addHeader(httpPost, header);

            response = client.execute(httpPost);
            responseModel = getResult(response);

            httpPost.releaseConnection();
        } catch (Exception e) {
            setErrorCode(responseModel, e);
        } finally {
            close(client, response);
        }
        return responseModel;
    }

    /**
     * 置消息头
     *
     * @param httpMessage
     * @param header
     */
    private static void addHeader(HttpMessage httpMessage, Map<String, String> header) {
        if (null != header && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpMessage.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 获取结果
     *
     * @param response
     * @return
     * @throws ParseException
     * @throws IOException
     */
    private static HttpResponseModel getResult(CloseableHttpResponse response) throws ParseException, IOException {
        HttpResponseModel responseModel = new HttpResponseModel();
        if (response != null) {
            responseModel.setCode(response.getStatusLine().getStatusCode());
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                String resp = EntityUtils.toString(httpEntity, CHARSET_UTF8);
                responseModel.setContent(resp);
            }
        }
        return responseModel;
    }

    /**
     * 设置错误代码
     *
     * @param responseModel
     * @param e
     */
    private static void setErrorCode(HttpResponseModel responseModel, Exception e) {
        if ((e instanceof SocketTimeoutException) || (e instanceof ConnectTimeoutException)) {
            responseModel.setCode(HttpURLConnection.HTTP_GATEWAY_TIMEOUT);
            logger.error("请求超时.", e);
        } else {
            responseModel.setCode(HttpURLConnection.HTTP_NOT_FOUND);
            logger.error("请求出错.", e);
        }
    }

    /**
     * 释放连接
     *
     * @param response
     */
    private static void relase(CloseableHttpResponse response) {
        if (response != null) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                logger.error("释放http连接出错：", e);
            }
        }
    }

    /**
     * 关闭链接
     *
     * @param client
     * @param response
     */
    private static void close(CloseableHttpClient client, CloseableHttpResponse response) {
        try {
            if (null != response) {
                relase(response);
            }
            if (null != client) {
                client.close();
            }
        } catch (IOException e) {
            logger.error("关闭http链接出错.", e);
        }
    }

    public static void main(String[] args) {
        Map<String, String> param = new HashMap<>();
        param.put("wd", "tany");
        HttpResponseModel responseModel = sendGet("http://www.baidu.com", param, null);
        System.out.println(responseModel);
    }
}
