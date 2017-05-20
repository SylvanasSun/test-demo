package com.sun.sylvanas.pattern.factory.test;

import com.sun.sylvanas.pattern.factory.simple.Car;
import com.sun.sylvanas.pattern.factory.simple.CarFactory;
import org.junit.Test;

/**
 * Created by SylvanasSun on 2017/5/20.
 */
public class CarFactoryTest {

    @Test
    public void testCreateCar() {
        Car car = CarFactory.createCar();
        car.drive();
    }

}
