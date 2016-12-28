package com.sun.sylvanas.utils;

import java.util.ResourceBundle;

/**
 * 使用反射根据类全路径生成对应的对象
 * <p>
 * Created by sylvanasp on 2016/12/28.
 */
public class BeanFactory {
    //用于加载配置文件
    private static ResourceBundle resourceBundle;

    static {
        resourceBundle = ResourceBundle.getBundle("instance");
    }

    /**
     * 根据类全路径生成对象
     *
     * @param className 类全路径
     * @param classType 对象的类型
     */
    public static <T> T getInstance(String className, Class<T> classType) {
        String name = BeanFactory.resourceBundle.getString(className);
        try {
            return (T) Class.forName(name).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
