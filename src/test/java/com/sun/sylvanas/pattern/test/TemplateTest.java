package com.sun.sylvanas.pattern.test;

import com.sun.sylvanas.pattern.template.Coffee;
import com.sun.sylvanas.pattern.template.RefreshBeverage;
import com.sun.sylvanas.pattern.template.Tea;
import org.junit.Test;

/**
 * Created by sylvanasp on 2016/8/30.
 */
public class TemplateTest {

    @Test
    public void test01() {
        RefreshBeverage coffee = new Coffee();
        coffee.prepareBeverageTemplate();

        System.out.println("----------------------");

        RefreshBeverage tea = new Tea();
        tea.prepareBeverageTemplate();
    }

}
