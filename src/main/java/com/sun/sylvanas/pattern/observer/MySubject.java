package com.sun.sylvanas.pattern.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟观察者模式的主题基类
 * Created by sylvanasp on 2016/8/12.
 */
public abstract class MySubject {

    // 初始化观察者列表
    protected List<MyObserver> observerList = new ArrayList<MyObserver>();

    /**
     * 注册观察者
     */
    public void register(MyObserver observer){
        observerList.add(observer);
    }

    /**
     * 移除观察者
     */
    public void detach(MyObserver observer) {
        observerList.remove(observer);
    }

    /**
     * 推送通知
     */
    public abstract void sendNotify(Object o);
}
