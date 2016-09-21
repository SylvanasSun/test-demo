package com.sun.sylvanas.jvm.example.concurrent;

/**
 * volatile变量自增运算测试.
 * 由于volatile变量只能保证可见性,在不符合以下两条规则的运算场景中,
 * 我们仍然要通过加锁(synchronized或java.util.concurrent中的原子类)来保证原子性:
 * 1. 运算结果并不依赖变量的当前值,或者能够确保只有单一的线程修改变量的值.
 * 2. 变量不需要与其他的状态变量共同参与不变约束.
 * <p>
 * Created by sylvanasp on 2016/9/21.
 */
public class VolatileTest {

    public static volatile int race = 0;

    public static void increase() {
        race++;
    }

    private static final int THREADS_COUNT = 20;

    /**
     * 这段代码发起了20个线程,每个线程对race变量进行10000次自增操作.
     * 如果代码能够正确并发的话,最后输出的结果应该为200000.
     * 但是运行这段代码并不会获得期望的结果,并且每次输出的结果都不一致(都是一个小于200000的数字).
     *
     * 这个问题的原因在于自增运算"race++"之中.这是由于Java中的运算非原子操作,导致volatile变量的运算在并发下一样是不安全的.
     * 使用Javap反编译这段代码后,会发现只有一行代码的increase()方法在Class文件中是由4条字节码指令构成的
     * (return指令不是由race++产生的,这条指令可以不计算),从字节码层面上很容易就分析出并发失败的原因:
     * 当getstatic指令把race的值取到操作栈顶时,volatile关键字保证了race的值在此时是正确的,
     * 但是在执行iconst_1、iadd这些指令的时候,其他线程可能已经把race的值加大了,而在操作栈顶的值就变成了过期的数据,
     * 所以putstatic指令执行后就可能把较小的race值同步回主内存之中.
     *
     */
    public static void main(String[] args) {
        Thread[] threads = new Thread[THREADS_COUNT];
        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        increase();
                    }
                }
            });
            threads[i].start();
        }

        // 等待所有累加线程都结束
        while (Thread.activeCount() > 1)
            Thread.yield();

        System.out.println(race);
    }

}
