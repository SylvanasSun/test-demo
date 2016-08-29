package com.sun.sylvanas.observer.test;

import com.sun.sylvanas.strategy.Pig;
import com.sun.sylvanas.strategy.Piglet;
import com.sun.sylvanas.strategy.RubberPig;
import org.junit.Test;

/**
 * Created by sylvanasp on 2016/8/29.
 */
public class StrategyTest {

    @Test
    public void test01() {
        Pig piglet = new Piglet();
        piglet.eat();
        piglet.grunt();

        System.out.println("-------------------------");

        Pig rubberPig = new RubberPig();
        rubberPig.eat();
        rubberPig.grunt();
    }

}
