package com.sun.sylvanas.strategy.impl;

import com.sun.sylvanas.strategy.EatStrategy;

/**
 * 不吃饭的策略
 *
 * Created by sylvanasp on 2016/8/29.
 */
public class NowayEat implements EatStrategy {

    public void eat() {
        System.out.println("No Eat..!");
    }

}
