package com.sun.sylvanas.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * HttpClient Utils
 * <p>
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
            return "";
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

    /**
     * 下载文件,返回文件路径
     */
    private static String download(HttpEntity entity, String dirPath, String suffix) {
        if (entity == null) {
            return "";
        }
        if (suffix == null || "".equals(suffix)) {
            throw new IllegalArgumentException("后缀名为空");
        }
        if (dirPath == null || "".equals(dirPath)) {
            throw new IllegalArgumentException("文件路径为空");
        }

        String fileName = "sun_" + random() + suffix;

        File file = new File(dirPath);
        if (file == null || !file.exists()) {
            file.mkdir();
        }

        //判断随机文件名是否重复,如果已存在则递归调用重新生成文件名
        String realPath = dirPath.concat(fileName);
        File realFile = new File(realPath);
        if (realFile.exists()) {
            download(entity, dirPath, suffix);
        }

        if (realFile == null || !realFile.exists()) {
            try {
                realFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedOutputStream out = null;
        InputStream input = null;

        try {
            input = entity.getContent();
            out = new BufferedOutputStream(new FileOutputStream(realFile));
            byte[] bytes = new byte[10 * 1024];
            int len;

            while ((len = input.read(bytes, 0, bytes.length)) != -1) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return realFile.toString();

    }

    /**
     * 随机生成数字并使用base64加密
     */
    private static String random() {
        //生成1-1000的随机数,并自定义添加0000
        int random = (int) (Math.random() * 1000 + 1) + 0000;

        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(String.valueOf(random).getBytes());
    }

}
