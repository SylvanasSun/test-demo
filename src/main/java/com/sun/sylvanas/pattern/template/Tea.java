package com.sun.sylvanas.pattern.template;

/**
 * Created by sylvanasp on 2016/8/30.
 */
public class Tea extends RefreshBeverage {

    @Override
    protected void brew() {
        System.out.println("泡制茶..!");
    }

    @Override
    protected void addCondiments() {
        System.out.println("不想加入调料..!");
    }

    /**
     * 因为不想执行addCondiments()函数,所以重写钩子函数.
     * 返回false
     */
    @Override
    protected boolean isCustomerWantsCondiments() {
        return false;
    }
}
