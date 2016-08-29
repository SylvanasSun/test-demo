package com.sun.sylvanas.strategy;

/**
 * 猪的抽象超类,所有猪都要继承此类
 * <p>
 * Created by sylvanasp on 2016/8/29.
 */
public abstract class Pig {

    // 注入叫声策略接口
    private GruntStrategy gruntStrategy;
    // 注入吃饭策略接口
    private EatStrategy eatStrategy;

    public void grunt() {
        this.gruntStrategy.grunt();
    }

    public void eat() {
        this.eatStrategy.eat();
    }

    public void setGruntStrategy(GruntStrategy gruntStrategy) {
        this.gruntStrategy = gruntStrategy;
    }

    public void setEatStrategy(EatStrategy eatStrategy) {
        this.eatStrategy = eatStrategy;
    }
}
