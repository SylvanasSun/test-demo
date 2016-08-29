package com.sun.sylvanas.strategy.impl;

import com.sun.sylvanas.strategy.GruntStrategy;

/**
 * 普通的猪叫策略
 *
 * Created by sylvanasp on 2016/8/29.
 */
public class CommonGrunt implements GruntStrategy {

    public void grunt() {
        System.out.println("Ho~ho~!");
    }

}
