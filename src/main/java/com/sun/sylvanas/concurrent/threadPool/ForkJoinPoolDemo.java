package com.sun.sylvanas.concurrent.threadPool;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Fork/Join的使用
 * <p>
 * Created by sylvanasp on 2016/12/3.
 */
public class ForkJoinPoolDemo {
    /**
     * 计算数列求和的Task,继承RecursiveTask(可以携带返回值的任务)
     */
    public static class CountTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10000;
        private long start;
        private long end;

        public CountTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long sum = 0;
            boolean canCompute = (end - start) < THRESHOLD;
            if (canCompute) {
                for (long i = start; i <= end; i++) {
                    sum += i;
                }
            } else {
                //分成100个任务
                long step = (start + end) / 100;
                ArrayList<CountTask> subTasks = new ArrayList<CountTask>();
                long pos = start;
                for (int i = 0; i < 100; i++) {
                    long lastOne = pos + step;
                    if (lastOne > end) {
                        lastOne = end;
                    }
                    CountTask subTask = new CountTask(pos, lastOne);
                    pos += step + 1;
                    subTasks.add(subTask);
                    subTask.fork();
                }
                for (CountTask t : subTasks) {
                    sum += t.join();
                }
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask task = new CountTask(0, 200000L);
        ForkJoinTask<Long> result = forkJoinPool.submit(task);
        try {
            Long res = result.get();
            System.out.println("sum=" + res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
