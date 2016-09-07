package com.sun.sylvanas.jvm.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * 线程死循环与死锁测试案例
 *
 * Created by sylvanasp on 2016/9/7.
 */
public class JStackThreadTest {


    /**
     * 线程死循环案例
     *
     * testBusyThread线程会一直在执行空循环.
     * 在空循环用尽全部执行时间直到线程切换,这种等待会消耗较多的CPU资源.
     *
     */
    public static void createBusyThread() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {

                }
            }
        },"testBusyThread");
        thread.start();
    }

    /**
     * 线程锁等待案例
     *
     * testLockThread线程在等待lock对象的notify或者notifyAll方法的出现.
     * 线程这时候处于WAITING状态,在被唤醒前不会被分配执行时间.
     * 只要lock对象的notify()或者notifyAll()方法被调用,这个线程便能激活以继续执行.
     *
     */
    public static void createLockThread(final Object lock) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"testLockThread");
        thread.start();
    }


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();
        createBusyThread();
        br.readLine();
        Object lock = new Object();
        createLockThread(lock);
    }

}
