package com.sun.sylvanas.pattern.factory.simple;

import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Created by SylvanasSun on 2017/5/20.
 */
public class CarFactory {

    private static final Logger logger = Logger.getLogger(CarFactory.class);
    private static ResourceBundle bundle;

    public static Car createCar() {
        Car car = null;
        String className = null;

        bundle = ResourceBundle.getBundle("car");
        className = bundle.getString("car1");

        try {
            car = (Car) Class.forName(className).newInstance();
            logger.info("Created car class is " + className);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("Instantiate car" + className + "falied", e);
        }
        return car;
    }

}
