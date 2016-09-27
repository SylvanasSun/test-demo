package com.sun.sylvanas.thread;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Volatile可见性案例
 * <p>
 * Created by sylvanasp on 2016/9/27.
 */
public class VolatileDemo {

    private static volatile int number = 0;

    private static ReentrantLock lock = new ReentrantLock();

    public static int getNumber() {
        return number;
    }

    /**
     * 由于volatile关键字只保证了可见性,并不保证原子性,所以需要互斥锁完成共享数据同步
     */
    public static void increase() {
        lock.lock();
        try {
            number++;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    increase();
                }
            }).start();
        }

        while (Thread.activeCount() > 1) {
            Thread.yield();
        }
        System.out.println(number);
    }

}
