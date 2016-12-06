package com.sun.sylvanas.concurrent.cas;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * AtomicStampedReference内部不仅维护了对象值,还维护了一个时间戳.
 * 当AtomicStampedReference的数值被修改时,除了更新数据本身外,还必须要更新时间戳.
 * 当AtomicStampedReference设置对象值时,对象值以及时间戳都必须满足期望值,写入才会成功.
 * <p>
 * // 比较设置,参数依次为: 期望值,写入新值,期望时间戳,新时间戳
 * public boolean compareAndSet(V expectedReference,V newReference,int expectedStamp,int new Stamp);
 * // 获得当前对象引用
 * public V getReference();
 * // 获得当前时间戳
 * public int getStamp();
 * // 设置当前对象引用和时间戳
 * public void set(V new Reference,int new Stamp);
 * <p>
 * Created by sylvanasp on 2016/12/6.
 */
public class AtomicStampedReferenceDemo {
    static AtomicStampedReference<Integer> money = new AtomicStampedReference<Integer>(19, 0);

    public static void main(String[] args) {
        //模拟多个线程同时更新后台数据,为用户充值
        for (int i = 0; i < 3; i++) {
            final int timestamp = money.getStamp();
            //用户充值线程
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        while (true) {
                            Integer m = money.getReference();
                            if (m < 20) {
                                if (money.compareAndSet(m, m + 20, timestamp, timestamp + 1)) {
                                    System.out.println("余额小于20元,充值成功,余额:"
                                            + money.getReference() + "元");
                                    break;
                                }
                            } else {
                                System.out.println("余额大于20元,无需充值!");
                                break;
                            }
                        }
                    }
                }
            }.start();
        }
        //用户消费线程
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    while (true) {
                        int timestamp = money.getStamp();
                        Integer m = money.getReference();
                        if (m > 10) {
                            System.out.println("余额大于10元");
                            if (money.compareAndSet(m, m - 10, timestamp, timestamp + 1)) {
                                System.out.println("成功消费10元,余额:" + money.getReference());
                                break;
                            }
                        } else {
                            System.out.println("余额不足!!!!!");
                            break;
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
