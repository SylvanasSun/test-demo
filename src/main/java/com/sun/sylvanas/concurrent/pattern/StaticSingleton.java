package com.sun.sylvanas.concurrent.pattern;

/**
 * 一个优雅的单例模式实现,利用内部类使其在getInstance()方法时第一次调用时才会创建实例.
 * <p>
 * Created by sylvanasp on 2016/12/7.
 */
public class StaticSingleton {
    private StaticSingleton() {
        System.out.println("StaticSingleton is create");
    }

    private static class SingletonHolder {
        private static StaticSingleton instance = new StaticSingleton();
    }

    public static StaticSingleton getInstance() {
        return SingletonHolder.instance;
    }
}
