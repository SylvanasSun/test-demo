package com.sun.sylvanas.concurrent.pattern.consumer_producer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 一个基于生产者-消费者模式的求整数平方的并行程序
 * <p>
 * Created by sylvanasp on 2016/12/7.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        //建立缓冲区
        LinkedBlockingQueue<PCData> queue = new LinkedBlockingQueue<PCData>(10);
        //建立生产者线程
        Producer producer1 = new Producer(queue);
        Producer producer2 = new Producer(queue);
        Producer producer3 = new Producer(queue);
        //建立消费者线程
        Consumer consumer1 = new Consumer(queue);
        Consumer consumer2 = new Consumer(queue);
        Consumer consumer3 = new Consumer(queue);
        //创建线程池
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(producer1);
        pool.execute(producer2);
        pool.execute(producer3);
        pool.execute(consumer1);
        pool.execute(consumer2);
        pool.execute(consumer3);
        Thread.sleep(10 * 1000);
        producer1.stop();
        producer2.stop();
        producer3.stop();
        Thread.sleep(3000);
        pool.shutdown();
    }
}
