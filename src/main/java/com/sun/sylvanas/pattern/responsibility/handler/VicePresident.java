package com.sun.sylvanas.pattern.responsibility.handler;

/**
 * 副总裁,处理50%以内的折扣请求.
 * Created by sylvanasp on 2016/8/30.
 */
public class VicePresident extends PriceHandler {

    @Override
    public void processDiscount(float discount) {
        if (discount <= 0.5) {
            System.out.format("%s批准了折扣:%.2f%n", this.getClass().getName(), discount);
        } else {
            successor.processDiscount(discount);
        }
    }

}
