package com.sun.sylvanas.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch是一个非常实用的多线程控制工具类.
 * 它通常用来控制线程等待,它可以让某一个线程等待直到倒计时结束,再开始执行.
 * <p>
 * Created by sylvanasp on 2016/11/30.
 */
public class CountDownLatchDemo implements Runnable {
    /**
     * 构造函数public CountDownLatch(int count);
     * 接收一个整数作为参数,即当前这个计数器的计数个数.
     * new CountDownLatch(10); 即需要10个线程完成任务,等待在CountDownLatch上的
     * 线程才能继续执行.
     */
    public static final CountDownLatch end = new CountDownLatch(10);
    public static final CountDownLatchDemo demo = new CountDownLatchDemo();

    public void run() {
        try {
            //模拟检查任务
            Thread.sleep(new Random().nextInt(10) * 1000);
            System.out.println("check complete");
            //通知CountDownLatch一个线程已经完成了任务,倒计时器-1
            end.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            threadPool.submit(demo);
        }
        //要求主线程等待CountDownLatch完成所有的任务
        end.await();
        //倒计时结束
        System.out.println("Hello,World!");
        threadPool.shutdown();
    }
}
