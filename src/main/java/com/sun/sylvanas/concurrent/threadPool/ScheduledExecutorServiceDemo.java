package com.sun.sylvanas.concurrent.threadPool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ScheduledExecutorService可以根据时间需要对线程进行调度管理.
 * 它包含3个方法:
 * schedule():会在给定时间,对任务进行调度
 * scheduleAtFixedRate():会对任务进行周期性的调度,它的调度频率是一定的.
 * scheduleWithFixedDelay():会对任务进行周期性的调度,它是在上一个任务结束后,再经过delay时间进行调度.
 * <p>
 * Created by sylvanasp on 2016/12/2.
 */
public class ScheduledExecutorServiceDemo {
    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
        //如果之前的任务没有完成,则调度也不会启动
        pool.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(System.currentTimeMillis() / 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
