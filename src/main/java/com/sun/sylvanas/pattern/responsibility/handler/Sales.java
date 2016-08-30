package com.sun.sylvanas.pattern.responsibility.handler;

/**
 * 销售类,用于处理5%以内的折扣
 * Created by sylvanasp on 2016/8/30.
 */
public class Sales extends PriceHandler {

    @Override
    public void processDiscount(float discount) {
        if (discount <= 0.05) {
            System.out.format("%s批准了折扣:%.2f%n", this.getClass().getName(), discount);
        } else {
            successor.processDiscount(discount);
        }
    }

}
