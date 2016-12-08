package com.sun.sylvanas.concurrent.pattern.future_jdk;

import java.util.concurrent.Callable;

/**
 * 使用JDK中的Future框架实现.
 * Callable接口只有一个call()方法,它会返回需要构造的实际数据
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public class RealData implements Callable<String> {
    private String para;

    public RealData(String para) {
        this.para = para;
    }

    public String call() throws Exception {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            sb.append(para);
            Thread.sleep(100);
        }
        return sb.toString();
    }
}
