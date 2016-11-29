package com.sun.sylvanas.concurrent.reentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition对象可以让线程在合适的时间等待,或者在某一个特定的时间得到通知继续执行.
 * 类似于Object.wait()&notify(),不过Condition是与重入锁相关联的.
 * Condition有以下几个重要的方法:
 * <p>
 * await():使当前线程等待,同时释放当前锁,当其他线程中使用signal()&signalAll()方法时,
 * 线程会重新获得锁并继续执行,或当线程被中断时,也能跳出等待.
 * <p>
 * awaitUninterruptibly():与await()方法基本相同,但是它并不会在等待过程中响应中断.
 * <p>
 * signal():用于唤醒一个等待中的线程.
 * <p>
 * signalAll():唤醒所有在等待中的线程.
 * Created by sylvanasp on 2016/11/29.
 */
public class ReentrantLockCondition implements Runnable {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    public void run() {
        try {
            lock.lock();
            condition.await();
            System.out.println("Thread is going on");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 在使用Condition.await()&signal()方法时,需要先持有相关的锁
     * signal()方法执行后,一般需要释放相关的锁.
     */
    public static void main(String[] args) throws InterruptedException {
        ReentrantLockCondition r1 = new ReentrantLockCondition();
        Thread t1 = new Thread(r1);
        t1.start();
        Thread.sleep(2000);
        //通知t1继续执行
        lock.lock();
        condition.signal();
        lock.unlock();
    }
}
