package com.sun.sylvanas.concurrent.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger是可变的,且线程安全的Integer.
 * 对其进行修改等任何操作,都是用CAS指令进行的.
 * <p>
 * public final int get() 取得当前值
 * public final void set(int newValue) 设置当前值
 * public final int getAndSet(int newValue) 设置新值,并返回旧值
 * public final boolean compareAndSet(int expect,int u) 如果当前值为expect,则设置为u
 * public final int getAndIncrement() 当前值加1,返回旧值
 * public final int getAndDecrement() 当前值减1,返回旧值
 * public final int getAndAdd(int delta) 当前值增加delta,返回旧值
 * public final int incrementAndGet() 当前值加1,返回新值
 * public final int decrementAndGet() 当前值减1,返回新值
 * public final int addAndGet(int delta) 当前值增加delta,返回新值
 * <p>
 * Created by sylvanasp on 2016/12/6.
 */
public class AtomicIntegerDemo {
    static AtomicInteger i = new AtomicInteger();

    public static class AddThread implements Runnable {
        public void run() {
            for (int k = 0; k < 10000; k++) {
                i.incrementAndGet();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int k = 0; k < 10; k++) {
            threads[k] = new Thread(new AddThread());
        }
        for (int k = 0; k < 10; k++) {
            threads[k].start();
        }
        for (int k = 0; k < 10; k++) {
            threads[k].join();
        }
        System.out.println(i);
    }
}
