package com.sun.sylvanas.concurrent.threadPool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 扩展ThradPoolExecutor,记录线程执行前与执行后的日志.
 * <p>
 * Created by sylvanasp on 2016/12/3.
 */
public class ExtThreadPool {
    public static class MyTask implements Runnable {
        private String name;
        private ReentrantLock lock;

        public MyTask(String name, ReentrantLock lock) {
            this.name = name;
            this.lock = lock;
        }

        public void run() {
            try {
                lock.lock();
                System.out.println("正在执行:Thread ID:" + Thread.currentThread().getId()
                        + ",Task Name=" + name);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS
                , new LinkedBlockingDeque<Runnable>()) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("准备执行:" + ((MyTask) r).name);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("执行完成:" + ((MyTask) r).name);
            }

            @Override
            protected void terminated() {
                System.out.println("线程池退出");
            }
        };
        for (int i = 0; i < 5; i++) {
            MyTask task = new MyTask("TASK-" + i, lock);
            poolExecutor.execute(task);
            Thread.sleep(10);
        }
        poolExecutor.shutdown();
    }
}
