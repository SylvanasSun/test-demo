package com.sun.sylvanas.json.json2java;

import org.json.JSONObject;

import java.util.Map;

/**
 * JSON In Java 案例Demo
 * Created by sylvanasp on 2016/8/31.
 */
public class JsonObjectExample {

    private static JSONObject jsonObject = new JSONObject();

    private static JSONObject jsonObjectMap;

    private static JSONObject jsonObjectBean;

    /**
     * 向JsonObject put 数据.
     */
    public static void put(String key,Object value) {
        jsonObject.put(key,value);
    }

    /**
     * 获得JsonObject
     */
    public static Object getJsonObject() {
        return jsonObject;
    }

    /**
     * 将当前的JsonObject转换为String返回.
     */
    public static String getString() {
        return jsonObject.toString();
    }



    /**
     * 转换map
     * boolean returnStr 如果为true则返回String,如果为false则返回JsonObject
     */
    public static Object mapToJSON(Map<String,Object> map,boolean returnStr) {
        jsonObjectMap = new JSONObject(map);
        if(returnStr) {
            return jsonObjectMap.toString();
        } else {
            return jsonObjectMap;
        }
    }

    /**
     * 转换javabean
     * boolean returnStr 如果为true则返回String,如果为false则返回JsonObject
     */
    public static Object beanToJSON(Object object,boolean returnStr) {
        jsonObjectBean = new JSONObject(object);
        if(returnStr) {
            return jsonObjectBean.toString();
        } else {
            return jsonObjectBean;
        }
    }



}
