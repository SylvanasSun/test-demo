package com.sun.sylvanas.jvm.example.clazz.loading;

/**
 *
 * 非法向前引用案例.
 * 编译器收集的顺序是由语句在源文件中出现的顺序所决定的,静态语句块中只能访问到定义在静态语句块之前的变量.
 * 定义在它之后的变量,在前面的静态语句块可以赋值,但是不能访问.
 *
 * Created by sylvanasp on 2016/9/12.
 */
public class IllegalForwardRef {

    static {
        i = 0; // 给定义在之后的变量赋值可以正常编译通过.
        System.out.println(1); // 编译器会提示"非法向前引用".
    }

    static int i = 1;

}
