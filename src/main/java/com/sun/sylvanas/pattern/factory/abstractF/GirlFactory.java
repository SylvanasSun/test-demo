package com.sun.sylvanas.pattern.factory.abstractF;

/**
 * Created by sylvanasp on 2016/8/29.
 */
public class GirlFactory implements PersonFactory {
    public Person getLittle() {
        return new LittleGirl();
    }

    public Person getBig() {
        return new BigGirl();
    }
}
