package com.sun.sylvanas.concurrent.cas;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * JDK提供了三种Updater:AtomicIntegerFieldUpdater,AtomicLongFieldUpdater,AtomicReferenceFieldUpdater.
 * 它们可以让你在不改动(或者极少改动)原有代码的基础上,让普通的变量也享受CAS操作带来的线程安全性.
 * 案例:
 * 假设某地要进行一次选举,模拟这个投票场景,如果选民投了候选人一票,就记为1,否则记为0.
 * 最终的选票显然就是所有数据的简单求和.
 * 注意事项:
 * 1.Updater只能修改它可见范围内的变量,因为Updater使用反射得到这个变量,如果变量不可见,
 * 则会出错. 例:如果score申明为private,就是不可行的.
 * <p>
 * 2.为了确保变量被正确的读取,它必须是volatile类型的.
 * <p>
 * 3.由于CAS操作会通过对象实例中的偏移量直接进行赋值,因此,它不支持static字段
 * (Unsafe.objectFieldOffset()不支持静态变量).
 * <p>
 * <p>
 * Created by sylvanasp on 2016/12/6.
 */
public class AtomicIntegerFieldUpdaterDemo {
    /**
     * 候选人类
     */
    public static class Candidate {
        int id;
        volatile int score;
    }

    public final static AtomicIntegerFieldUpdater<Candidate> scoreUpdater =
            AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");
    //检查Updater是否工作正确
    public static AtomicInteger allScore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final Candidate stu = new Candidate();
        Thread[] threads = new Thread[10000];
        for (int i = 0; i < 10000; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    if (Math.random() > 0.4) {
                        scoreUpdater.incrementAndGet(stu);
                        allScore.incrementAndGet();
                    }
                }
            };
            threads[i].start();
        }
        for (int i = 0; i < 10000; i++) {
            threads[i].join();
        }
        System.out.println("score=" + stu.score);
        System.out.println("allScore=" + allScore);
    }
}
