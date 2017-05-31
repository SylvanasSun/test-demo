package com.sun.sylvanas.application.thread;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SylvanasSun on 2017/5/31.
 */
public class SimpleWorkQueueThreadPool {

    private BlockingQueue<Runnable> queue;
    private Object lock = new Object();
    private WorkThread[] pool;
    private AtomicInteger count;
    private static final int DEFAULT_THREAD_NUMBER = Runtime.getRuntime().availableProcessors();
    private static final String CLASS_NAME = SimpleWorkQueueThreadPool.class.getName();

    public SimpleWorkQueueThreadPool() {
        this(new LinkedBlockingQueue<Runnable>(), DEFAULT_THREAD_NUMBER);
    }

    public SimpleWorkQueueThreadPool(BlockingQueue<Runnable> queue, int threadSize) {
        this.queue = queue;
        this.pool = new WorkThread[threadSize];
        this.count = new AtomicInteger(0);
        for (int i = 0; i < threadSize; i++) {
            pool[i] = new WorkThread();
            new Thread(pool[i]).start();
        }
    }

    public void execute(Runnable r) {
        synchronized (lock) {
            queue.add(r);
            lock.notifyAll();
        }
    }

    private class WorkThread implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setName(CLASS_NAME + count.getAndIncrement());
            Runnable r;
            while (true) {
                synchronized (lock) {
                    while (queue.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    r = queue.poll();
                }
                System.out.println(Thread.currentThread().getName() + " execute work!");
                r.run();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimpleWorkQueueThreadPool threadPool = new SimpleWorkQueueThreadPool();
        final int[] count = {0};
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if ("add thread".equalsIgnoreCase(s)) {
                for (int i = 0; i < 10; i++) {
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 100; i++)
                                count[0]++;
                        }
                    });
                }
            } else if ("end".equalsIgnoreCase(s))
                break;
        }
        System.out.println("count : " + count[0]);
    }

}
