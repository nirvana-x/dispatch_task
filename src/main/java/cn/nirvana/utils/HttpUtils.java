package cn.nirvana.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/5/15 11:25
 **/
public class HttpUtils {

    /**
     * 发起Post请求
     *
     * @param url     接口地址
     * @param params  json格式的报文
     * @param timeout 超时时间
     * @return
     * @throws UnsupportedEncodingException
     */
    public static final String postHttp(String url, String params, int timeout)
            throws UnsupportedEncodingException {
        String responseMsg = "";
        // 构造HttpClient的实例
        HttpClient httpClient = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
        // 设置编码格式
        httpClient.getParams().setContentCharset("UTF-8");
        // 构造PostMethod的实例
        PostMethod postMethod = new PostMethod(url);
        // postMethod.set
        postMethod.setRequestHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        postMethod.setRequestBody(params);
        postMethod.setRequestHeader("Connection", "close");
        try {
            // 执行postMethod,调用http接口
            httpClient.executeMethod(postMethod);
            // 读取内容
            responseMsg = postMethod.getResponseBodyAsString();
        } catch (HttpException e) {
            e.printStackTrace();
            throw new UnsupportedEncodingException(e.getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnsupportedEncodingException(e.getClass().getName());
        } finally {
            // 释放连接
            postMethod.releaseConnection();
            httpClient.getHttpConnectionManager().closeIdleConnections(0);
        }
        return responseMsg;

    }


}
