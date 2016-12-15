package com.sun.sylvanas.concurrent.java8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 使用函数式编程的风格优雅地处理异常.
 * CompletableFuture提供了一个异常处理方法exceptionally();
 * <p>
 * Created by sylvanasp on 2016/12/15.
 */
public class CompletableFutureDemo3 {
    public static Integer calc(Integer param) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return param / 0;
    }

    /**
     * 使用thenCompose组合多个CompletableFuture
     */
    public static void compose() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> calc(60))
                .exceptionally(ex -> {
                    System.out.println(ex.toString());
                    return 0;
                })
                //将处理后的结果传递给thenCompose(),并进一步传递给后续新生成的CompletableFuture
                .thenCompose((i) -> CompletableFuture.supplyAsync(() -> calc(i)))
                .exceptionally(ex -> {
                    System.out.println(ex.toString());
                    return 0;
                })
                .thenApply((i) -> Integer.toString(i))
                .thenApply((str) -> "\"" + str + "\"")
                .thenAccept(System.out::println);
        future.get();
    }

    /**
     * 使用tehnCombine组合多个CompletableFuture
     */
    public static void combine() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> intFuture1 = CompletableFuture.supplyAsync(() -> calc(60))
                .exceptionally(ex -> {
                    System.out.println(ex.toString());
                    return 0;
                });
        CompletableFuture<Integer> intFuture2 = CompletableFuture.supplyAsync(() -> calc(50))
                .exceptionally(ex -> {
                    System.out.println(ex.toString());
                    return 0;
                });
        //使用thenCombine将intFuture1和intFuture2的执行结果进行累加((i,j) -> (i+j))
        CompletableFuture<Void> combineFuture = intFuture1.thenCombine(intFuture2, (i, j) -> (i + j))
                .thenApply((str) -> "\"" + str + "\"")
                .thenAccept(System.out::println);
        combineFuture.get();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> calc(60))
                .exceptionally(ex -> {
                    System.out.println(ex.toString());
                    return 0;
                })
                .thenApply((i) -> Integer.toString(i))
                .thenApply((str) -> "\"" + str + "\"")
                .thenAccept(System.out::println);
        future.get();
    }

}
