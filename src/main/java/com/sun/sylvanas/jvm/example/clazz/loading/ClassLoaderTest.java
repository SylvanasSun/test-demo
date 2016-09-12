package com.sun.sylvanas.jvm.example.clazz.loading;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类加载器测试
 *
 * Created by sylvanasp on 2016/9/12.
 */
public class ClassLoaderTest {

    public static void main(String[] args) throws Exception {

        /**
         * 构造了一个简单的类加载器,它可以加载与自己在同一路径下的Class文件.
         */
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";

                    InputStream inputStream = getClass().getResourceAsStream(fileName);
                    if(inputStream == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[inputStream.available()];
                    inputStream.read(b);
                    return defineClass(name,b,0,b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };

        Object obj = myLoader.loadClass("com.sun.sylvanas.jvm.example.clazz.loading.ClassLoaderTest")
                .newInstance();

        /**
         * 第一句验证了这个对线确实是类com.sun.sylvanas.jvm.example.clazz.loading.ClassLoaderTest
         * 实例化出的对象.
         *
         * 第二句与类com.sun.sylvanas.jvm.example.clazz.loading.ClassLoaderTest做所属类型检查时返回false.
         * 这是因为在虚拟机中存在了两个ClassLoaderTest类,一个是由系统应用程序类加载器加载的,另外一个
         * 是由我们自定义的类加载器加载的,虽然都来自同一个Class文件,但依然是两个独立的类,所以返回false.
         *
         */
        System.out.println(obj.getClass());
        System.out.println(obj instanceof com.sun.sylvanas.jvm.example.clazz.loading.ClassLoaderTest);

    }

}
