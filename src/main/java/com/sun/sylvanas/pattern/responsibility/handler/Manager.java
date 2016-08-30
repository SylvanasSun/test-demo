package com.sun.sylvanas.pattern.responsibility.handler;

/**
 * 销售经理,处理30%以内的折扣请求
 * Created by sylvanasp on 2016/8/30.
 */
public class Manager extends PriceHandler {

    @Override
    public void processDiscount(float discount) {
        if (discount <= 0.3) {
            System.out.format("%s批准了折扣:%.2f%n", this.getClass().getName(), discount);
        } else {
            successor.processDiscount(discount);
        }
    }

}
