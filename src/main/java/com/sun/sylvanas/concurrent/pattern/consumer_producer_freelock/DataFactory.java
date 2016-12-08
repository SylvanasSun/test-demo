package com.sun.sylvanas.concurrent.pattern.consumer_producer_freelock;

import com.lmax.disruptor.EventFactory;

/**
 * 一个产生Data对象的工厂,它2会在Disruptor系统初始化时,构造所有缓冲区中的对象实例
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public class DataFactory implements EventFactory<Data> {
    public Data newInstance() {
        return new Data();
    }
}
