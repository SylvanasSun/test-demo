package com.sun.sylvanas.concurrent.java8;

import java.util.Random;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * LongAccumulator和LongAdder内部优化是一样的,它们的区别是前者可以实现任意函数操作.
 * 它们将内部核心数据value分离成一个数组,每个线程访问时,通过哈希等算法映射到其中一个数字进行计数.
 * 而最终的计数结果,则为这个数组的求和累加.
 * 内部也使用了sun.misc.Contended注解解决了伪共享问题,使用这个注解需要虚拟机参数:
 * -XX:-RestrictContented,否则,这个注解将被忽略.
 * LongAccumulator Construction Method:
 * public LongAccumulator(LongBinaryOperator accumulatorFunction,Long identity)
 * 参数1:需要执行的二元函数(接收两个long形参数并返回long)
 * 参数2:初始值
 * <p>
 * Created by sylvanasp on 2016/12/16.
 */
public class LongAccumulatorDemo {
    /**
     * 使用LongAccumulator通过多线程访问若干个整数,并返回遇到的最大的数字.
     */
    public static void main(String[] args) throws InterruptedException {
        LongAccumulator accumulator = new LongAccumulator(Long::max, Long.MIN_VALUE);
        Thread[] threads = new Thread[1000];

        for (int i = 0; i < 1000; i++) {
            threads[i] = new Thread(() -> {
                Random random = new Random();
                long value = random.nextLong();
                //LongAccumulator通过Long::max识别最大值并且保存在内部.
                accumulator.accumulate(value);
            });
            threads[i].start();
        }
        for (int i = 0; i < 1000; i++) {
            threads[i].join();
        }
        //通过longValue()函数对所有的cell进行Long::max操作,得到最大值.
        System.out.println(accumulator.longValue());
    }
}
