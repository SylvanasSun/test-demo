package com.sun.sylvanas.concurrent.java8;

import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock是Java8中引入的一种新的锁机制,可以认为它是读写锁的改进版本.
 * 读写锁虽然分离了读和写,使得读与读之间可以完全并发.但是,读和写之间依然会阻塞.
 * StampedLock提供了一种乐观的读策略,这种乐观的锁非常类似无锁的操作,使得乐观锁完全不会阻塞写线程.
 * <p>
 * Created by sylvanasp on 2016/12/15.
 */
public class StampedLockDemo {
    static class Point {
        private double x, y;
        private final StampedLock stampedLock = new StampedLock();

        public void move(double deltaX, double deltaY) { //这是一个排它锁
            long stamp = stampedLock.writeLock();
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                stampedLock.unlockWrite(stamp);
            }
        }

        public double distanceFromOrigin() { //只读方法
            //tryOptimisticRead()表示试图尝试一次乐观读,它返回的时间戳可以作为这一次锁获取的凭证.
            long stamp = stampedLock.tryOptimisticRead();
            double currentX = x, currentY = y;
            //validate()判断这个stamp是否在读过程发生期间被修改过.
            //如果没有被修改过(true),则认为这次读取是有效的.
            //反之,如果stamp是不可用的(false),则意味着有可能出现脏读.
            //遇见这种情况,我们可以像处理CAS操作那样在一个死循环中一直使用乐观读,直到成功为止.
            //在本例中,采用了升级乐观锁级别的办法,将乐观锁变为悲观锁.
            if (!stampedLock.validate(stamp)) {
                //获得悲观的读锁
                stamp = stampedLock.readLock();
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    stampedLock.unlockRead(stamp);
                }
            }
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }
    }
}
