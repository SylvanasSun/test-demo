package com.sun.sylvanas.pattern.responsibility.handler;

/**
 * CEO,处理55%以内的折扣请求,如果超出55%,则拒绝请求.
 * Created by sylvanasp on 2016/8/30.
 */
public class CEO extends PriceHandler {

    @Override
    public void processDiscount(float discount) {
        if (discount <= 0.55) {
            System.out.format("%s批准了折扣:%.2f%n", this.getClass().getName(), discount);
        } else {
            System.out.format("%s拒绝了折扣:%.2f%n", this.getClass().getName(), discount);
        }
    }

}
