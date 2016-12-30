package com.sun.sylvanas.pattern.test;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by sylvanasp on 2016/12/30.
 */
public class CommonTest {
    @Test
    public void test01() {
        String[] strings = {"hello", "world", "atomicity", "consistency", "isolate", "durability"};
        Arrays.stream(strings).forEach((i) -> System.out.println(i));
    }
}
