package com.sun.sylvanas.pattern.responsibility;

import com.sun.sylvanas.pattern.responsibility.handler.PriceHandler;

/**
 * 客户类,用于发起折扣请求.
 * Created by sylvanasp on 2016/8/30.
 */
public class Customer {

    private PriceHandler priceHandler;

    public void setPriceHandler(PriceHandler priceHandler) {
        this.priceHandler = priceHandler;
    }

    /**
     * 发送折扣请求
     */
    public void requestDiscount(float discount) {
        priceHandler.processDiscount(discount);
    }

}
