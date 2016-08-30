package com.sun.sylvanas.pattern.factory;

/**
 * Created by sylvanasp on 2016/8/29.
 */
public class AnimalFactory {

    public static Animal createAnimal(String className) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        return (Animal) Class.forName(className).newInstance();
    }

}
