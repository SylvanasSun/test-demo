package com.sun.sylvanas.concurrent.java8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * CompletableFuture是Java8新增的一个超大型工具类.它实现了Future接口和CompletionStage接口.
 * CompletionStage接口也是Java8新增的,它拥有多达约40种方法.
 * CompletionStage是为了函数式编程中的流式调用准备的.
 * 通过CompletionStage接口,我们可以在一个执行结果上进行多次流式调用,以此可以得到最终结果.
 * <p>
 * Created by sylvanasp on 2016/12/14.
 */
public class CompletableFutureDemo {

    /**
     * 通过CompletableFuture,我们可以手动设置CompletableFuture的完成状态.
     */
    public static class AskThread implements Runnable {
        CompletableFuture<Integer> result = null;

        public AskThread(CompletableFuture<Integer> result) {
            this.result = result;
        }

        public void run() {
            int temp = 0;
            try {
                //这里会阻塞,因为CompletableFuture中根本没有它所需要的数据,CompletableFuture处于未完成状态.
                temp = result.get() * result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("result:" + temp);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        new Thread(new AskThread(future)).start();
        //模拟长时间的计算过程
        Thread.sleep(1000);
        //告知完成结果，AskThread可以继续执行
        future.complete(60);
    }
}
