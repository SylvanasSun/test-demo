package com.sun.sylvanas.pattern.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by sylvanasp on 2016/8/31.
 */
public class CglibProxy implements MethodInterceptor {

    private Enhancer enhancer = new Enhancer();

    /**
     * 获得代理的实例
     */
    public Object getProxy(Class clazz) {
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);

        return enhancer.create();
    }

    /**
     * Object proxy 目标代理类
     * method 目标方法
     * args 方法参数
     * methodProxy 方法代理
     */
    public Object intercept(Object proxy, Method method, Object[] args,
                            MethodProxy methodProxy) throws Throwable {
        System.out.println("开始记录日志......");
        methodProxy.invokeSuper(proxy,args);
        System.out.println("日志记录完毕..!");
        return null;
    }
}
