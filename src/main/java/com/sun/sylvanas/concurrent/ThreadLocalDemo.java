package com.sun.sylvanas.concurrent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal即一个线程的局部变量,它是一个容器,用于保存只有当前线程才可以访问的数据,
 * 从而实现线程安全的.
 * <p>
 * Created by sylvanasp on 2016/12/5.
 */
public class ThreadLocalDemo {
    public static ThreadLocal<SimpleDateFormat> t1 = new ThreadLocal<SimpleDateFormat>();

    public static class ParseDate implements Runnable {
        int i = 0;

        public ParseDate(int i) {
            this.i = i;
        }

        public void run() {
            /**
             * ThreadLocal为每一个线程分配不同的对象,是需要在应用层面保证的,
             * ThreadLocal只是起到一个简单的容器作用.
             */
            if (t1.get() == null) {
                t1.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            }
            try {
                Date date = t1.get().parse("2016-12-5 15:46:" + i % 60);
                System.out.println(i + ":" + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>());
        for (int i = 0; i < 100; i++) {
            pool.execute(new ParseDate(i));
        }
    }
}
