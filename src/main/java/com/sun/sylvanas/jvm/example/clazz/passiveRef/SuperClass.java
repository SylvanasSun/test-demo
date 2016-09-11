package com.sun.sylvanas.jvm.example.clazz.passiveRef;

/**
 *
 * 被动初始化类案例
 *
 * 通过子类引用父类的静态字段,不会导致子类初始化.
 *
 * Created by sylvanasp on 2016/9/11.
 */
public class SuperClass {

    static {
        System.out.println("SuperClass init!");
    }

    public static int value = 123;

}
