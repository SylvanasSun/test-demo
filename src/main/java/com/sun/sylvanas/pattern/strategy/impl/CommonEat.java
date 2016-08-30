package com.sun.sylvanas.pattern.strategy.impl;

import com.sun.sylvanas.pattern.strategy.EatStrategy;

/**
 * 普通的吃饭策略
 *
 * Created by sylvanasp on 2016/8/29.
 */
public class CommonEat implements EatStrategy {

    public void eat() {
        System.out.println("吃饭ing...!");
    }

}
