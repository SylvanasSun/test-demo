package com.sun.sylvanas.jvm.example.clazz.loading;

/**
 * 虚拟机会保证一个类的<clinit>()方法在多线程环境中被正确地加锁、同步,
 * 如果多个线程同时去初始化一个类,那么只会有一个线程去执行这个类的<clinit>()方法,
 * 其他线程都需要阻塞等待,直到活动线程执行<clinit>()方法完毕.
 * 如果在一个类的<clinit>()方法中有耗时很长的操作,就可能导致多个进程阻塞.
 * 如果执行<clinit>()方法的那条线程退出<clinit>()方法后,其他线程唤醒之后不会再次进入<clinit>()方法.
 * 同一个类加载器下,一个类型只会初始化一次.
 *
 * Created by sylvanasp on 2016/9/12.
 */
public class DeadLoopClass {

    static {
        // 如果不加上这个if语句,编译器将提示"Initializer does not complete normally" 并拒绝编译.
        if(true) {
            System.out.println(Thread.currentThread() + "init DeadLoopClass");
            while (true) {

            }
        }
    }

    /**
     * 一条线程在死循环以模拟长时间操作,另外一条线程在阻塞等待.
     */
    public static void main(String[] args) {
        Runnable script = new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread() + "start");
                DeadLoopClass dlc = new DeadLoopClass();
                System.out.println(Thread.currentThread() + "run over");;
            }
        };

        Thread thread1 = new Thread(script);
        Thread thread2 = new Thread(script);
        thread1.start();
        thread2.start();
    }

}
