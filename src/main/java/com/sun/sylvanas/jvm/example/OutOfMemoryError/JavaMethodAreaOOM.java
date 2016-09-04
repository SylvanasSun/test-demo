package com.sun.sylvanas.jvm.example.OutOfMemoryError;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 方法区内存溢出
 * 方法区是用于存放Class的相关信息的,所以要让方法区产生内存溢出的基本思路为:
 * 在运行时产生大量的类去填满方法区,直到溢出.
 * 所以可以使用CGLib直接操作字节码运行时生成大量的动态类.
 *
 * VM Args : -XX:PermSize=10M -XX:MaxPermSize=10M
 *
 * Created by sylvanasp on 2016/9/4.
 */
public class JavaMethodAreaOOM {

    static class OOMObject {

    }

    public static void main(final String[] args) {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                public Object intercept(Object o, Method method, Object[] objects,
                                        MethodProxy methodProxy) throws Throwable {
                    return methodProxy.invokeSuper(o,objects);
                }
            });
            enhancer.create();
        }
    }

}
