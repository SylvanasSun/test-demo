package com.sun.sylvanas.concurrent.algorithm.sort;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 希尔排序算法:
 * 它将整个数组根据间隔h分割为若干个子数组,子数组相互穿插在一起,
 * 每一次排序时,分别对每一个子数组进行排序,每次排序时,总是交换间隔为h的两个元素.
 * 在每一组排序完成后,可以递减h的值,进行下轮更加精细的排序.直到h为1,此时等价于一次插入排序.
 * 由于希尔排序每次都针对不同 的子数组进行排序,各个子数组之间是独立的,所以适合并行.
 * <p>
 * Created by sylvanasp on 2016/12/10.
 */
public class ShellSort {
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    /**
     * 希尔排序线程,根据给定的起始位置和h,对子数组进行排序
     */
    private static class ShellSortTask implements Runnable {
        int i = 0;
        int h = 0;
        CountDownLatch latch;
        int[] array;

        public ShellSortTask(int i, int h, CountDownLatch latch, int[] array) {
            this.i = i;
            this.h = h;
            this.latch = latch;
            this.array = array;
        }

        @SuppressWarnings("Duplicates")
        public void run() {
            if (array[i] < array[i - h]) {
                int temp = array[i];
                int j = i - h;
                while (j >= 0 && array[j] > temp) {
                    array[j + h] = array[j];
                    j -= h;
                }
                array[j + h] = temp;
            }
            latch.countDown();
        }
    }

    /**
     * 希尔排序并行实现
     */
    @SuppressWarnings("Duplicates")
    public static void pShellSort(int[] array) throws InterruptedException {
        //计算出最大的h值
        int h = 1;
        CountDownLatch latch = null;
        while (h <= array.length / 3) {
            h = h * 3 + 1;
        }
        while (h > 0) {
            System.out.println("h=" + h);
            if (h >= 4) {
                latch = new CountDownLatch(array.length - h);
            }
            for (int i = h; i < array.length; i++) {
                //控制线程数量,在h大于等于4时使用并行线程
                if (h >= 4) {
                    pool.execute(new ShellSortTask(i, h, latch, array));
                } else {
                    if (array[i] < array[i - h]) {
                        int temp = array[i];
                        int j = i - h;
                        while (j >= 0 && array[j] > temp) {
                            array[j + h] = array[j];
                            j -= h;
                        }
                        array[j + h] = temp;
                    }
                }
            }
            //等待线程排序完成,进入下一次排序
            latch.await();
            //计算出下一个h值
            h = (h - 1) / 3;
        }
    }

    /**
     * 希尔排序串行实现
     */
    @SuppressWarnings("Duplicates")
    public static void shellSort(int[] array) {
        //计算出最大的h值(间隔)
        int h = 1;
        while (h <= array.length / 3) {
            h = h * 3 + 1;
        }
        while (h > 0) {
            for (int i = h; i < array.length; i++) {
                if (array[i] < array[i - h]) {
                    int temp = array[i];
                    int j = i - h;
                    while (j >= 0 && array[j] > temp) {
                        array[j + h] = array[j];
                        j -= h;
                    }
                    array[j + h] = temp;
                }
            }
            //计算出下一个h值
            h = (h - 1) / 3;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int[] array = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        ShellSort.pShellSort(array);
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            if (array[i] != array[array.length - 1]) {
                System.out.print(array[i] + ",");
            } else {
                System.out.print(array[i]);
            }
        }
        System.out.print("]");
    }
}
