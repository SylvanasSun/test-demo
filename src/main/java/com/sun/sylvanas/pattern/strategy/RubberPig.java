package com.sun.sylvanas.pattern.strategy;

import com.sun.sylvanas.pattern.strategy.impl.NowayEat;
import com.sun.sylvanas.pattern.strategy.impl.NowayGrunt;

/**
 * Created by sylvanasp on 2016/8/29.
 */
public class RubberPig extends Pig {

    public RubberPig() {
        super();
        super.setEatStrategy(new NowayEat());
        super.setGruntStrategy(new NowayGrunt());
    }

}
