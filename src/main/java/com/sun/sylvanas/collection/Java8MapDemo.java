package com.sun.sylvanas.collection;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Java8为Map新增的方法演示Demo(只有个别几例)
 * Created by sylvanasp on 2016/12/28.
 */
public class Java8MapDemo {
    @Test
    public void test01() {
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        //尝试替换key为four的value,由于原map没有对应的key,map不会发生改变,也不会添加新的key-value对.
        map.replace("four", 4);
        System.out.println(map);
        //使用原value与传入参数计算出来的结果覆盖原有的value(13)
        map.merge("three", 10,
                (oldValue, param) -> oldValue + param);
        System.out.println(map);
        //当key为“four”对应的value为null时(或不存在),使用计算的结果作为新value
        map.computeIfAbsent("four", (key) -> key.length());
        System.out.println(map); //添加了four=4这组键值对
        //当key为“four”对应的value存在时,使用计算的结果作为新value
        map.computeIfPresent("four",
                (key, value) -> value * value);
        System.out.println(map); //four=4 变为 four=16
    }
}
