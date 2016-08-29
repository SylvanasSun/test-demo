package com.sun.sylvanas.observer.test;

import com.sun.sylvanas.factory.Animal;
import com.sun.sylvanas.factory.AnimalFactory;
import com.sun.sylvanas.factory.ReadProperties;
import com.sun.sylvanas.factory.abstractF.BoyFactory;
import com.sun.sylvanas.factory.abstractF.GirlFactory;
import com.sun.sylvanas.factory.abstractF.PersonFactory;
import org.junit.Test;

/**
 * 工厂模式案例测试类
 * Created by sylvanasp on 2016/8/29.
 */
public class FactoryTest {

    @Test
    public void test01() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Animal dog = AnimalFactory.createAnimal(ReadProperties.read("dog"));
        dog.sayHello();
        Animal cat = AnimalFactory.createAnimal(ReadProperties.read("cat"));
        cat.sayHello();
    }

    @Test
    public void test02() {
        PersonFactory boyFactory = new BoyFactory();
        boyFactory.getLittle().sayHello();
        boyFactory.getBig().sayHello();

        PersonFactory girlFactory = new GirlFactory();
        girlFactory.getLittle().sayHello();
        girlFactory.getBig().sayHello();
    }

}
