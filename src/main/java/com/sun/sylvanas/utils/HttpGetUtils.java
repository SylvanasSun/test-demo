package com.sun.sylvanas.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sylvanasp on 2016/10/25.
 */
public class HttpGetUtils {

    /**
     * Get Method
     */
    public static String get(String url) {
        if (url == null || "".equals(url)) {
            throw new IllegalArgumentException(url + " 为空 ");
        }
        String result = "";
        //获取HttpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //获得get方法实例
        HttpGet httpGet = new HttpGet(url);
        try {
            //执行方法,返回响应
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                if (response != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    System.out.println(response.getStatusLine());
                    HttpEntity entity = response.getEntity();
                    result = readResponse(entity, "utf-8");
                }
            } finally {
                response.close();
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * stream读取内容，可以传入字符格式
     */
    private static String readResponse(HttpEntity entity, String charset) {
        if (charset == null || "".equals(charset)) {
            throw new IllegalArgumentException("编码为空");
        }
        StringBuilder sBuilder = new StringBuilder();
        BufferedReader reader = null;
        if (entity == null) {
            return null;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
            String line = null;

            while ((line = reader.readLine()) != null) {
                sBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sBuilder.toString();
    }

}
