package com.sun.sylvanas.pattern.test;

import com.sun.sylvanas.pattern.responsibility.Customer;
import com.sun.sylvanas.pattern.responsibility.handler.PriceHandlerFactory;
import org.junit.Test;

import java.util.Random;

/**
 * 责任链模式测试
 * Created by sylvanasp on 2016/8/30.
 */
public class CORTest {

    @Test
    public void test01() {
        Customer customer = new Customer();
        customer.setPriceHandler(PriceHandlerFactory.createPriceHandler());

        // 使用Random模拟折扣数
        Random random = new Random();

        for (int i = 0; i < 200; i++) {
            System.out.print(i + ":");
            customer.requestDiscount(random.nextFloat());
        }
    }

}
