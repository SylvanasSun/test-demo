package com.sun.sylvanas.concurrent.pattern.future_jdk;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * JDK中Future框架使用Demo.
 * JDK还为Future接口提供了以下一些简单的控制功能:
 * boolean cancel(boolean mayInterruptIfRunning); //取消任务
 * boolean isCancelled(); //是否已经取消
 * boolean isDone(); //是否已经完成
 * V get() throws InterruptedException,ExecutionException //取得返回对象
 * V get(long timeout,TimeUnit unit) //取得返回对象,可以设置超时时间
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public class FutureMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //构造FutureTask
        FutureTask<String> future = new FutureTask<String>(new RealData("a"));
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        //开启线程进行RealData.call()
        threadPool.submit(future);

        System.out.println("请求完毕");
        try {
            //模拟额外的业务逻辑操作
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("数据 = " + future.get());
    }
}
