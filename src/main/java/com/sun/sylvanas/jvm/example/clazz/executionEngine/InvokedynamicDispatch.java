package com.sun.sylvanas.jvm.example.clazz.executionEngine;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * 子类访问祖类方法案例
 * <p>
 * invokedynamic的分派逻辑不是由虚拟机决定的,而是由程序员决定的.
 * <p>
 * Created by sylvanasp on 2016/9/13.
 */
public class InvokedynamicDispatch {

    class GrandFather {
        void thinking() {
            System.out.println("I am grandfather.");
        }
    }

    class Father extends GrandFather {
        void thinking() {
            System.out.println("I am Father.");
        }
    }

    class Son extends Father {
        void thinking() {
            try {
                MethodType mt = MethodType.methodType(void.class);
                MethodHandle mh = lookup().findSpecial(GrandFather.class,"thinking",mt,getClass());
                mh.invoke(this);
            } catch (Throwable e) {

            }
        }
    }

    public static void main(String[] args) {
        (new InvokedynamicDispatch().new Son()).thinking();
    }

}
