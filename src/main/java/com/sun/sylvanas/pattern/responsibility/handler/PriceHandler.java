package com.sun.sylvanas.pattern.responsibility.handler;

/**
 * 折扣处理器
 * Created by sylvanasp on 2016/8/30.
 */
public abstract class PriceHandler {

    /**
     * 直接后继,用于传递请求.
     */
    protected PriceHandler successor;

    public void setSuccessor(PriceHandler successor) {
        this.successor = successor;
    }

    /**
     * 处理折扣请求的抽象方法
     */
    public abstract void processDiscount(float discount);
}
