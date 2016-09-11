package com.sun.sylvanas.jvm.example.clazz.passiveRef;

/**
 *
 *
 *
 * Created by sylvanasp on 2016/9/11.
 */
public class ConstClass {

    static {
        System.out.println("ConstClass init!");
    }

    public static final String HELLOWORLD = "Hello,World";

}
