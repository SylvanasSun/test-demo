package com.sun.sylvanas.thread;

import java.util.Scanner;

/**
 * 使用继承Thread与实现Runnable两种方式演示售票案例.
 * <p>
 * Created by sylvanasp on 2016/9/25.
 */
public class TicketDemo {

    static class MyThread extends Thread {

        private int ticketCount = 5;

        private String threadName;

        MyThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            synchronized (MyThread.class) {
                while (ticketCount > 0) {
                    ticketCount--;
                    System.out.println("售票处 " + threadName + "售出了一张票,还剩余" + ticketCount + "张票.");
                }
            }
        }
    }

    static class MyRunnalbe implements Runnable {

        private int ticketCount = 5;

        public void run() {
            synchronized (MyRunnalbe.class) {
                while (ticketCount > 0) {
                    ticketCount--;
                    System.out.println("售票处 " + Thread.currentThread().getName() +
                            "售出了一张票,还剩余" + ticketCount + "张票.");
                }
            }
        }
    }

    /**
     * 守护进程是服务于用户线程的,当所有用户线程关闭后,守护线程也会关闭.
     */
    static class MyDaemon implements Runnable {
        public void run() {
            for (int i = 0; i < 100; i++) {
                System.out.println("守护进程运行中,time:" + System.currentTimeMillis());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        MyThread th1 = new MyThread("1");
//        MyThread th2 = new MyThread("2");
//        MyThread th3 = new MyThread("3");
//
//        th1.start();
//        th2.start();
//        th3.start();

        MyRunnalbe runnalbe = new MyRunnalbe();

        MyDaemon myDaemon = new MyDaemon();

        new Thread(runnalbe, "1").start();
        new Thread(runnalbe, "2").start();
        new Thread(runnalbe, "3").start();

        Thread thread = new Thread(myDaemon, "守护进程");
        thread.setDaemon(true);
        thread.start();

        Scanner sc = new Scanner(System.in);
        sc.next();

        System.out.println("守护进程结束.");

    }

}
