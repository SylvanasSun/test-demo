package com.sun.sylvanas.pattern.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 记录Car类日志的调用处理器
 * Created by sylvanasp on 2016/8/31.
 */
public class CarLogInvocationHandler implements InvocationHandler {

    /**
     * 通过构造方法注入目标类.
     */
    public CarLogInvocationHandler(Object target) {
        super();
        this.target = target;
    }

    private Object target;


    /**
     *
     * Object proxy 目标代理类
     * Method method 目标方法
     * Object[] args 方法参数
     *
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        long startTime = System.currentTimeMillis();
        System.out.println("开始记录日志.....");
        method.invoke(target);
        long endTime = System.currentTimeMillis();
        System.out.println("记录完毕..共耗时: " + (endTime - startTime) + "毫秒.");
        return null;
    }

}
