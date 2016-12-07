package com.sun.sylvanas.concurrent.pattern.consumer_producer;

/**
 * 生产者和消费者之间的共享数据模型
 * <p>
 * Created by sylvanasp on 2016/12/7.
 */
public final class PCData {
    private final int intData;

    public PCData(int d) {
        intData = d;
    }

    public PCData(String d) {
        intData = Integer.valueOf(d);
    }

    public int getIntData() {
        return intData;
    }

    @Override
    public String toString() {
        return "data:" + intData;
    }
}
