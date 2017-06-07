package com.sun.sylvanas.application.hello_aop;

/**
 * Created by SylvanasSun on 2017/6/7.
 */
public class Something {

    public void say() {
        System.out.println("Say something...");
    }

    public static void main(String[] args) {
        Something something = new Something();
        something.say();
    }

}
