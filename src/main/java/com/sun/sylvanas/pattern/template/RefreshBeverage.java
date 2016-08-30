package com.sun.sylvanas.pattern.template;

/**
 * 饮料的抽象基类
 * <p>
 * Created by sylvanasp on 2016/8/30.
 */
public abstract class RefreshBeverage {

    /**
     * 制备饮料的基本模板方法
     */
    public final void prepareBeverageTemplate() {
        // 1.将水煮沸
        boilWater();
        // 2.泡制饮料
        brew();
        // 3.将饮料倒入杯中
        pourInCup();
        if (isCustomerWantsCondiments()) {
            // 4.加入调味料
            addCondiments();
        }
    }

    /**
     * 基本方法 将饮料倒入杯中
     */
    protected void pourInCup() {
        System.out.println("将饮料倒入杯中..!");
    }

    /**
     * 基本方法 将水煮沸
     */
    protected void boilWater() {
        System.out.println("水已经煮沸了..!");
    }

    /**
     * 泡制饮料,需要子类自定义实现
     */
    protected abstract void brew();

    /**
     * 加入调味料,需要子类自定义实现
     */
    protected abstract void addCondiments();

    /**
     * 钩子函数,默认返回为true.
     * 用于是否执行加入调料,具体的子类可以自定义是否挂钩及挂钩条件.
     */
    protected boolean isCustomerWantsCondiments() {
        return true;
    }
}
