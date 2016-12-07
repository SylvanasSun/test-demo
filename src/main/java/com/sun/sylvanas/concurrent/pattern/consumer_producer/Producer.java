package com.sun.sylvanas.concurrent.pattern.consumer_producer;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者线程,它构建PCData对象,并放入BlockingQueue队列中.
 * <p>
 * Created by sylvanasp on 2016/12/7.
 */
public class Producer implements Runnable {
    private volatile boolean isRunning = true;
    private BlockingQueue<PCData> queue; //公共数据缓冲区
    private static AtomicInteger count = new AtomicInteger(); //总数
    private static final int SLEEPTIME = 1000;

    public Producer(BlockingQueue<PCData> queue) {
        this.queue = queue;
    }

    public void stop() {
        isRunning = false;
    }

    public void run() {
        PCData data = null;
        Random random = new Random();

        System.out.println("start Producer id=" + Thread.currentThread().getId());
        try {
            while (isRunning) {
                Thread.sleep(random.nextInt(SLEEPTIME));
                data = new PCData(count.incrementAndGet());
                System.out.println(data + "is put into queue");
                if (!queue.offer(data, 2, TimeUnit.SECONDS)) { //提交数据到缓冲区
                    System.out.println("failed to put data: " + data);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
