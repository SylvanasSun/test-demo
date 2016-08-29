package com.sun.sylvanas.factory.abstractF;

/**
 * Created by sylvanasp on 2016/8/29.
 */
public class BoyFactory implements PersonFactory {

    public Person getLittle() {
        return new LittleBoy();
    }

    public Person getBig() {
        return new BigBoy();
    }
}
