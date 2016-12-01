package com.sun.sylvanas.concurrent.threadPool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 固定线程池Demo
 * <p>
 * Created by sylvanasp on 2016/12/1.
 */
public class FixedPoolDemo {
    public static class MyTask implements Runnable {
        private static final ReentrantLock lock = new ReentrantLock();
        public static final CountDownLatch latch = new CountDownLatch(5);

        public void run() {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getId() + ": My job done!");
                latch.countDown();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        MyTask myTask = new MyTask();
        for (int i = 0; i < 5; i++) {
            pool.submit(myTask);
        }
        myTask.latch.await();
        System.out.println("所有任务已执行完毕!");
        pool.shutdown();
    }
}
