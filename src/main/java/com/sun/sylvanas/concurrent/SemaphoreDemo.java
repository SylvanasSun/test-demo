package com.sun.sylvanas.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 信号量是对锁的扩展,它可以指定多个线程,同时访问一个资源.
 * 构造Semaphore对象时必须需要指定信号量的准入数,即同时能申请多少个许可
 * <p>
 * Created by sylvanasp on 2016/11/30.
 */
public class SemaphoreDemo implements Runnable {
    /**
     * 申明了一个包含5个许可的信号量,即同时可以有5个线程进入run方法执行部分
     */
    public final Semaphore semaphore = new Semaphore(5);

    public void run() {
        try {
            /**
             * acquire()申请信号量,在离开时必须使用release()释放信号量.
             */
            semaphore.acquire();
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getId() + ": My Job done!");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启20个线程,系统会以5个线程(所定义的信号量包含的线程数)一组为单位,依次输出内容.
     */
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        SemaphoreDemo semaphoreDemo = new SemaphoreDemo();
        for (int i = 0; i < 20; i++) {
            pool.execute(semaphoreDemo);
        }
    }
}
