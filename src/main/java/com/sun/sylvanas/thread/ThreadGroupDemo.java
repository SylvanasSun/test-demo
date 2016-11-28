package com.sun.sylvanas.thread;

/**
 * 线程组使用Demo
 * <p>
 * Created by sylvanasp on 2016/11/28.
 */
public class ThreadGroupDemo {

    public static class Thread1 implements Runnable {
        public void run() {
            String name = Thread.currentThread().getThreadGroup().getName()
                    + "-" + Thread.currentThread().getName();
            while (true) {
                System.out.println("I am " + name);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ThreadGroup group01 = new ThreadGroup("Group01");
        Thread t1 = new Thread(group01,new Thread1(),"t1");
        Thread t2 = new Thread(group01,new Thread1(),"t2");
        t1.start();
        t2.start();
        System.out.println(group01.activeCount());
        group01.list();
    }

}
