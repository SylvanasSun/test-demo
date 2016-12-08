package com.sun.sylvanas.concurrent.pattern.consumer_producer_freelock;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sylvanasp on 2016/12/8.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        DataFactory dataFactory = new DataFactory();
        //设置缓冲区大小为1024(必须为2的整数次方)
        int bufferSize = 1024;

        Disruptor<Data> disruptor = new Disruptor<Data>(dataFactory, bufferSize, threadPool,
                ProducerType.MULTI, new BlockingWaitStrategy());
        //设置消费者
        disruptor.handleEventsWithWorkerPool(
                new Consumer(),
                new Consumer(),
                new Consumer(),
                new Consumer());
        disruptor.start();

        RingBuffer<Data> ringBuffer = disruptor.getRingBuffer();
        Producer producer = new Producer(ringBuffer);
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            byteBuffer.putLong(0, l);
            producer.pushData(byteBuffer);
            Thread.sleep(100);
            System.out.println("add data" + l);
        }
    }
}
