package com.sun.sylvanas.strategy;

import com.sun.sylvanas.strategy.impl.CommonEat;
import com.sun.sylvanas.strategy.impl.CommonGrunt;

/**
 *
 * Created by sylvanasp on 2016/8/29.
 */
public class Piglet extends Pig {

   public Piglet() {
       super();
       // 注入策略接口实现
       super.setEatStrategy(new CommonEat());
       super.setGruntStrategy(new CommonGrunt());
   }

}
