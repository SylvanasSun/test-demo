package com.sun.sylvanas.pattern.test;

import com.sun.sylvanas.pattern.proxy.Car;
import com.sun.sylvanas.pattern.proxy.CarLogInvocationHandler;
import com.sun.sylvanas.pattern.proxy.CglibProxy;
import com.sun.sylvanas.pattern.proxy.Moveable;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by sylvanasp on 2016/8/31.
 */
public class ProxyTest {

    @Test
    public void test01() throws InterruptedException {
        Moveable car = new Car();
        CarLogInvocationHandler invocationHandler = new CarLogInvocationHandler(car);

        Moveable carProxy = (Moveable) Proxy.newProxyInstance(car.getClass().getClassLoader(),
                car.getClass().getInterfaces(), invocationHandler);
        carProxy.move();
    }

    @Test
    public void test02() throws InterruptedException {
        Moveable car = new Car();
        CglibProxy cglibProxy = new CglibProxy();
        Moveable carProxy = (Moveable) cglibProxy.getProxy(car.getClass());
        carProxy.move();
    }

    @Test
    public void test03() throws InterruptedException {
        final Moveable car = new Car();
        Moveable carProxy = (Moveable) Proxy.newProxyInstance(car.getClass().getClassLoader(),
                car.getClass().getInterfaces(), new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("Hello...");
                        method.invoke(car,args);
                        System.out.println("World!");
                        return null;
                    }
                });
        carProxy.move();
    }

}
