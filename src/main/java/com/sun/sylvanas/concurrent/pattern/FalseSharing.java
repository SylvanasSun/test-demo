package com.sun.sylvanas.concurrent.pattern;

/**
 * 解决伪共享问题:
 * 使X变量的前后空间都先占据一定的位置(padding),这样当内容被读入缓冲时,
 * 这个缓冲行中,只有X变量一个变量是实际有效的,因此就不会发生多个线程同时修改
 * 缓存行中不同变量而导致变量全体失效的情况.
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public final class FalseSharing implements Runnable {
    private final static int NUM_THREADS = 4;
    private final static long ITERATIONS = 500L * 1000L * 1000L;
    //数组元素个数和线程数一致,每个线程都会访问自己对应的longs中的元素
    private static VolatileLong[] longs = new VolatileLong[NUM_THREADS];
    private final int arrayIndex;

    static {
        for (int i = 0; i < longs.length; i++) {
            longs[i] = new VolatileLong();
        }
    }

    public FalseSharing(final int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public void run() {
        long i = ITERATIONS + 1;
        while (0 != --i) {
            longs[arrayIndex].value = i;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final long start = System.currentTimeMillis();
        runTest();
        System.out.println("共耗时:" + (System.currentTimeMillis() - start) + "ms");
    }

    private static void runTest() throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new FalseSharing(i));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    /**
     * 准备了7个long型变量用于填充缓存
     * 只有VolatileLong.value是会被使用的,
     * p1,p2..是用于将数组中第一个VolatileLong.value和第二个VolatileLong.value分开,
     * 防止它们进入同一个缓存行.
     */
    private final static class VolatileLong {
        public volatile long value = 0L;
        public long p1, p2, p3, p4, p5, p6, p7;
    }
}
