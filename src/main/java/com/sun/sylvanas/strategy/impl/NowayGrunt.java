package com.sun.sylvanas.strategy.impl;

import com.sun.sylvanas.strategy.GruntStrategy;

/**
 * 不猪叫的策略
 *
 * Created by sylvanasp on 2016/8/29.
 */
public class NowayGrunt implements GruntStrategy {

    public void grunt() {
        System.out.println(".........");
    }

}
