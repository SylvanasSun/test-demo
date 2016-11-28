package com.sun.sylvanas.concurrent.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁中断锁的功能
 * 即:在等待锁的过程中,程序可以根据需要取消对锁的请求
 * <p>
 * Created by sylvanasp on 2016/11/28.
 */
public class IntLock implements Runnable {

    public static ReentrantLock lock1 = new ReentrantLock();
    public static ReentrantLock lock2 = new ReentrantLock();
    int lock;

    public IntLock(int lock) {
        this.lock = lock;
    }

    /**
     * lockInterruptibly()是一个可以对中断进行响应的锁申请动作
     * 在等待锁的过程中,可以响应中断
     */
    public void run() {
        try {
            if (lock == 1) {
                lock1.lockInterruptibly();
                Thread.sleep(500);
                lock2.lockInterruptibly();
            } else {
                lock2.lockInterruptibly();
                Thread.sleep(500);
                lock1.lockInterruptibly();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock1.isHeldByCurrentThread()) {
                lock1.unlock();
            }
            if (lock2.isHeldByCurrentThread()) {
                lock2.unlock();
            }
            System.out.println(Thread.currentThread().getId() + ":线程退出");
        }
    }

    /**
     * 当t1和t2启动后:
     * t1会先占用lock1,再占用lock2
     * t2会先占用lock2,再请求lock1
     * 这样会很容易造成t1和t2之间的相互等待,形成死锁.
     * 使用重入锁的lockInterruptibly()可以响应中断,在65行中
     * t2线程进行中断,所以t2会放弃对lock1的申请,同时释放已经获得的lock2
     * 这个操作可以让t1线程顺利得到lock2而继续执行下去.
     */
    public static void main(String[] args) throws InterruptedException {
        IntLock l1 = new IntLock(1);
        IntLock l2 = new IntLock(2);
        Thread t1 = new Thread(l1);
        Thread t2 = new Thread(l2);
        t1.start();
        t2.start();
        Thread.sleep(1000);
        //中断其中一个线程
        t2.interrupt();
    }
}
