package com.sun.sylvanas.jvm.example.clazz.executionEngine;

/**
 * 方法静态解析案例
 * 其中,静态方法sayHello()只可能属于类型StaticResolution
 * 没有任何手段可以覆盖或隐藏这个方法.
 *
 * 使用javap命令查看这段程序的字节码,会发现sayHello()方法是由invokestatic指令调用的.
 *
 * Created by sylvanasp on 2016/9/13.
 */
public class StaticResolution {

    public static void sayHello() {
        System.out.println("Hello World!");
    }

    public static void main(String[] args) {
        StaticResolution.sayHello();
    }

}
