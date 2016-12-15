package com.sun.sylvanas.concurrent.java8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 实现Future模式的异步调用
 * <p>
 * Created by sylvanasp on 2016/12/14.
 */
public class CompletableFutureDemo2 {
    public static Integer calc(Integer para) {
        try {
            //模拟长时间的计算
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return para * para;
    }

    /**
     * 流式调用案例
     */
    public static void streamInvoke() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> calc(60))
                .thenApply((i) -> Integer.toString(i))
                .thenApply((str) -> "\"" + str + "\"")
                .thenAccept(System.out::println);
        future.get();
    }

    /**
     * 使用CompletableFuture.supplyAsync()构造一个CompletableFuture实例.
     * 在supplyAsync()函数中,它会在一个新的线程中,执行传入的参数.
     * 在CompletableFuture中,有以下几个类似的工厂方法:
     * public <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);
     * public <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,Executor executor):
     * public CompletableFuture<Void> runAsync(Runnable runnable);
     * public CompletableFuture<Void> runAsync(Runnable runnable,Executor executor);
     * <p>
     * supplyAsync()用于需要有返回值的场景,runAsync()用于没有返回值的场景.
     * 参数Executor,可以在指定的线程池中工作,如果不指定,则默认在系统公共的ForkJoinPool.common线程池中执行.
     * ForkJoinPool.commonPool()中的公共线程池的所有线程都是Daemon线程.
     * 这些线程无论是否执行完成,在主线程退出后,都会关闭.
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final CompletableFuture<Integer> future =
                CompletableFuture.supplyAsync(() -> calc(60));
        System.out.println(future.get());
    }
}
