package com.sun.sylvanas.pattern.observer;

/**
 * 观察者接口
 * Created by sylvanasp on 2016/8/12.
 */
public interface MyObserver {

    /**
     * 基于push模型
     */
    public void update(Object content);

    /**
     * 基于pull模型
     */
    public void update(MySubject subject);

}
