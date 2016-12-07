package com.sun.sylvanas.concurrent.cas;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * JDK提供了三种原子数组:AtomicIntegerArray,AtomicLongArray,AtomicReferenceArray.
 * AtomicIntegerArray本质上是对int[]类型的封装,使用Unsafe类通过CAS的方式控制int[]在多线程下的安全性.
 * API:
 * //获得数组第i个下标的元素
 * public final int get(int i)
 * //获得数组的长度
 * public final int length()
 * //将数组第i个下标设置为newValue,并返回旧值
 * public final int getAndSet(int i,int newValue)
 * //进行CAS操作,如果第i个下标的元素等于expect,则设置为update,设置成功返回true
 * public final boolean compareAndSet(int i,int expect,int update)
 * //将第i个下标的元素加1,并返回旧值
 * public final int getAndIncrement(int i)
 * //将第i个下标的元素减1,并返回旧值
 * public final int getAndDecrement(int i)
 * //将第i个下标的元素增加delta(delta可以是负数),并返回旧值
 * public final int getAndAdd(int i,int delta)
 * <p>
 * Created by sylvanasp on 2016/12/6.
 */
public class AtomicIntegerArrayDemo {
    static AtomicIntegerArray array = new AtomicIntegerArray(10);

    public static class AddThread implements Runnable {
        public void run() {
            for (int i = 0; i < 10000; i++) {
                array.getAndIncrement(i % array.length());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new AddThread());
        }
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
        System.out.println(array);
        long end = System.currentTimeMillis();
        System.out.println("总耗时:" + (end - start) + "ms");
    }
}
