package com.sun.sylvanas.concurrent.reentrantLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁:
 * 锁申请等待限时
 * <p>
 * Created by sylvanasp on 2016/11/28.
 */
public class TimeLock implements Runnable {
    public static ReentrantLock lock = new ReentrantLock();

    public void run() {
        try {
            /**
             * tryLock()方法接收两个参数:
             * 1.表示等待时长
             * 2.表示计时单位
             * 以下设置为等待5秒,如果超过5秒还没有获得锁,则返回false
             * 由于Thread.sleep(6000);休眠了6秒,所以另一个线程无法在5秒内获得锁
             * 所以请求锁会失败
             */
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                Thread.sleep(6000);
            } else {
                System.out.println("get lock failed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        TimeLock timeLock = new TimeLock();
        Thread t1 = new Thread(timeLock);
        Thread t2 = new Thread(timeLock);
        t1.start();
        t2.start();
    }
}
