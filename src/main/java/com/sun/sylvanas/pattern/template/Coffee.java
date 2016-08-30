package com.sun.sylvanas.pattern.template;

/**
 *
 * Created by sylvanasp on 2016/8/30.
 */
public class Coffee extends RefreshBeverage {

    @Override
    protected void brew() {
        System.out.println("泡制咖啡..!");
    }

    @Override
    protected void addCondiments() {
        System.out.println("加入糖和牛奶..!");
    }

}
