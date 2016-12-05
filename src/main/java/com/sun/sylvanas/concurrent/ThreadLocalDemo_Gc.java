package com.sun.sylvanas.concurrent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 跟踪ThreadLocal对象以及内部SimpleDateFormat对象的垃圾回收
 *
 * Created by sylvanasp on 2016/12/5.
 */
public class ThreadLocalDemo_Gc {
    static volatile ThreadLocal<SimpleDateFormat> t1 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected void finalize() throws Throwable {
            System.out.println(this.toString() + "is gc");
        }
    };
    static volatile CountDownLatch countDownLatch = new CountDownLatch(10000);

    public static class ParseDate implements Runnable {
        int i = 0;

        public ParseDate(int i) {
            this.i = i;
        }

        public void run() {
            if (t1.get() == null) {
                t1.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {
                    @Override
                    protected void finalize() throws Throwable {
                        System.out.println(this.toString() + "is gc");
                    }
                });
                System.out.println(Thread.currentThread().getId()
                        + ":create SimpleDateFormat");
            }
            try {
                Date date = t1.get().parse("2016-12-05 16:00:" + i % 60);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    /**
     * 先后进行两次任务提交,每次10000个任务,在第一次任务结束后
     * 将ThreadLocal设置为null,接着进行一次GC.
     * 在第二次任务结束后,再进行一次GC.
     */
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; i++) {
            pool.execute(new ParseDate(i));
        }
        countDownLatch.await();
        System.out.println("mission complete!");
        t1 = null;
        System.gc();
        System.out.println("first GC complete!");
        //在设置ThreadLocal的时候,会清除ThreadLocalMap中的无效对象
        t1 = new ThreadLocal<SimpleDateFormat>();
        countDownLatch = new CountDownLatch(10000);
        for (int i = 0; i < 10000; i++) {
            pool.execute(new ParseDate(i));
        }
        countDownLatch.await();
        Thread.sleep(1000);
        System.gc();
        System.out.println("second GC complete!");
    }
}
