package com.sun.sylvanas.pattern.factory.simple;

/**
 * Created by SylvanasSun on 2017/5/20.
 */
@CarType(typeName = "Lamborghini")
public class Lamborghini extends Car {

    @Override
    public void drive() {
        System.out.println("I'm driving a lamborghini!");
    }

}
