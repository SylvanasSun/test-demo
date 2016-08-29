package com.sun.sylvanas.factory;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 读取Properties文件工具类,并封装到Map中返回.
 * <p>
 * Created by sylvanasp on 2016/8/29.
 */
public class ReadProperties {

    private static Map<String, String> map = new HashMap<String, String>();

    private static final String PROPERTIES_NAME = "animal";

    private static ResourceBundle resourceBundle;

    static {
        resourceBundle = ResourceBundle.getBundle(PROPERTIES_NAME);
    }

    /**
     * 读取Properties文件,返回整个Map集合.
     */
    public static Map<String, String> getPropertiesMap() {
        Enumeration<String> enumeration = resourceBundle.getKeys();
        // 遍历properties文件,并封装到map.
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = resourceBundle.getString(key);
            map.put(key, value);
            System.out.println(key + " - " + value);
        }
        return map;
    }

    /**
     * 读取Properties文件,并返回对应key的类名.
     */
    public static String read(String key) {
        if (!"".equals(key) && key != null) {
            return resourceBundle.getString(key);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        getPropertiesMap();
        System.out.println(read("dog"));
    }

}
