package com.sun.sylvanas.concurrent.threadPool;

import java.util.concurrent.*;

/**
 * 自定义了一个线程池和拒绝策略
 * <p>
 * Created by sylvanasp on 2016/12/2.
 */
public class RejectThreadPoolDemo {
    public static class MyTask implements Runnable {
        public void run() {
            System.out.println("name:" + Thread.currentThread().getName() + " id:"
                    + Thread.currentThread().getId());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask task = new MyTask();
        /**
         * public ThreadPoolExecutor(int corePoolSize,
         *                          int maximumPoolSize,
         *                          long keepAliveTime,
         *                          TimeUnit unit,
         *                          BlockingQueue<Runnable> workQueue,
         *                          ThreadFactory threadFactory,
         *                          RejectedExecutionHandler handler)
         *
         * corePoolSize:线程池中的线程数量.
         * maximumPoolSize:线程池中的最大线程数量.
         * keepAliveTime:当线程池数量超过corePoolSize时,多余的空闲线程的存活时间,
         * 即,超过corePoolSize的空闲线程,在多长时间内,会被销毁.
         * unit:keepAliveTime的单位.
         * workQueue:任务等待队列,被提交尚未被执行的任务.
         * threadFactory:线程工厂,用于创建线程,一般使用默认的即可.
         * handler:拒绝策略,当任务太多来不及处理时,如何拒绝任务.
         *
         * 自定义了一个有5个常驻线程,且最大线程量也为5的线程池.
         * 使用了大小为10的无界任务等待队列,
         * 自定义了拒绝策略(打印被丢弃的任务信息)
         */
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>(10),
                Executors.defaultThreadFactory(),
                new RejectedExecutionHandler() {
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println(r.toString() + "is discard!");
                    }
                });
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            poolExecutor.submit(task);
            Thread.sleep(10);
        }
    }
}
