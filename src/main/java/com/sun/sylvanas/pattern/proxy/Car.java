package com.sun.sylvanas.pattern.proxy;

import java.util.Random;

/**
 * Created by sylvanasp on 2016/8/31.
 */
public class Car implements Moveable {

    public void move() throws InterruptedException {
        Thread.sleep(new Random().nextInt(1000));
        System.out.println("汽车行驶中...!");
    }

}
