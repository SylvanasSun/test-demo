package com.sun.sylvanas.concurrent.pattern.consumer_producer_freelock;

/**
 * Created by sylvanasp on 2016/12/8.
 */
public class Data {
    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
