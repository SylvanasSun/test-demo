package com.sun.sylvanas.pattern.factory.simple;

/**
 * Created by SylvanasSun on 2017/5/20.
 */
@CarType(typeName = "Ferrari")
public class Ferrari extends Car {

    @Override
    public void drive() {
        System.out.println("I'm drving a Ferrari!");
    }

}
