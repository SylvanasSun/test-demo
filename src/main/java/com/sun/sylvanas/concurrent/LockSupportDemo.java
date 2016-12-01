package com.sun.sylvanas.concurrent;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport是一个线程阻塞工具,它可以在线程内任意位置让线程阻塞
 * LockSupport是可以响应中断的,它不会抛出异常,但可以从Thread.interrupted()等方法中获得中断标记
 * <p>
 * Created by sylvanasp on 2016/12/1.
 */
public class LockSupportDemo {
    public static Object u = new Object();

    public static class Thread1 implements Runnable {
        public void run() {
            synchronized (u) {
                System.out.println("in " + Thread.currentThread().getName());
                LockSupport.park();
                if (Thread.interrupted()) {
                    System.out.println(Thread.currentThread().getName() + "被中断了!");
                }
            }
            System.out.println(Thread.currentThread().getName() + "执行结束!");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Thread1(), "t1");
        Thread t2 = new Thread(new Thread1(), "t2");
        t1.start();
        Thread.sleep(100);
        t2.start();
        t1.interrupt();
        LockSupport.unpark(t2);
    }
}
