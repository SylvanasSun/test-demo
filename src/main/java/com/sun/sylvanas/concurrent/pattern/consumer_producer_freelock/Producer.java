package com.sun.sylvanas.concurrent.pattern.consumer_producer_freelock;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;


/**
 * 生产者线程
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public class Producer {
    private final RingBuffer<Data> ringBuffer; //环形缓冲区

    public Producer(RingBuffer<Data> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * pushData()接收一个ByteBuffer对象,在ByteBuffer中可以用来包装任何数据类型.
     * pushData()将传入的ByteBuffer中的数据提取出来,并装载到环形缓冲区中.
     */
    public void pushData(ByteBuffer byteBuffer) {
        long sequence = ringBuffer.next(); //得到下一个可用的序列号
        try {
            Data event = ringBuffer.get(sequence);//通过序列号取得下一个可用的Data
            event.setValue(byteBuffer.getLong(0));
        } finally {
            ringBuffer.publish(sequence);//发布数据
        }
    }
}
