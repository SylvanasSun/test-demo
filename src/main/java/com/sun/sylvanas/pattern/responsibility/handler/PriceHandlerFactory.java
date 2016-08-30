package com.sun.sylvanas.pattern.responsibility.handler;

/**
 * Created by sylvanasp on 2016/8/30.
 */
public class PriceHandlerFactory {

    public static PriceHandler createPriceHandler() {

        PriceHandler sales = new Sales();
        PriceHandler lead = new Lead();
        PriceHandler man = new Manager();
        PriceHandler dir = new Director();
        PriceHandler vp = new VicePresident();
        PriceHandler ceo = new CEO();

        // 设置后继
        sales.setSuccessor(lead);
        lead.setSuccessor(man);
        man.setSuccessor(dir);
        dir.setSuccessor(vp);
        vp.setSuccessor(ceo);

        return sales;
    }

}
