package com.sun.sylvanas.concurrent.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁:
 * ReentrantLock可以通过以下构造方法构建一个公平锁
 * 公平锁是不会产生饥饿现象的,但是由于要维护一个有序队列,性能上十分低下
 * public ReentrantLock(boolean fair)
 * <p>
 * Created by sylvanasp on 2016/11/29.
 */
public class FairLock implements Runnable {
    public static ReentrantLock lock = new ReentrantLock(true);

    public void run() {
        while (true) {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + "获得锁!");
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        FairLock fairLock = new FairLock();
        Thread thread1 = new Thread(fairLock, "Thread_1");
        Thread thread2 = new Thread(fairLock, "Thread_2");
        thread1.start();
        thread2.start();
    }
}
