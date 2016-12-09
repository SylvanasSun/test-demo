package com.sun.sylvanas.concurrent.algorithm.sort;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 奇偶交换排序算法:
 * 它将排序过程分为两个阶段:奇交换和偶交换.
 * 对于奇交换来说,它总是比较奇数索引以及其相邻的后续元素.
 * 而偶交换总是比较偶数索引和其相邻的后续元素.
 * 并且,奇交换和偶交换会成对出现,这样才能保证比较和交换涉及到数组中的每一个元素.
 * <p>
 * 由于将整个比较交换独立分割为奇阶段和偶阶段,这使得在每一个阶段内,所有的比较和交换是没有
 * 数据相关性的,因此,每一次比较和交换都可以独立运行.也就可以并行化了.
 * <p>
 * Created by sylvanasp on 2016/12/9.
 */
public class OddEvenSort {
    static int exchFlag = 1;
    static ExecutorService pool = Executors.newCachedThreadPool();

    static synchronized void setExchFlag(int v) {
        exchFlag = v;
    }

    static synchronized int getExchFlag() {
        return exchFlag;
    }

    /**
     * 奇偶交换排序任务类,它的主要工作是进行数据比较和必要的交换
     */
    public static class OddEvenSortTask implements Runnable {
        int i;
        CountDownLatch latch;
        int[] arr;

        public OddEvenSortTask(int i, CountDownLatch latch, int[] arr) {
            this.i = i;
            this.latch = latch;
            this.arr = arr;
        }

        public void run() {
            if (arr[i] > arr[i + 1]) {
                int temp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = temp;
                setExchFlag(1);
            }
            latch.countDown();
        }
    }

    /**
     * 奇偶交换排序并行实现
     * 使用CountDownLatch记录线程数量
     * 对于每一次迭代,使用单独的线程对每一次元素比较和交换进行操作.
     * 在下一次迭代开始前,必须等待上一次迭代所有线程的完成.
     */
    public static void pOddEventSort(int[] arr) throws InterruptedException {
        int start = 0;
        while (getExchFlag() == 1 || start == 1) {
            setExchFlag(0);
            //偶数的数组长度,当start为1时,只有len/2-1个线程
            CountDownLatch latch = new CountDownLatch
                    (arr.length / 2 - (arr.length % 2 == 0 ? start : 0));
            for (int i = start; i < arr.length - 1; i += 2) {
                pool.submit(new OddEvenSortTask(i, latch, arr));
            }
            //等待所有线程结束
            latch.await();
            if (start == 0) {
                start = 1;
            } else {
                start = 0;
            }
        }
    }

    /**
     * 奇偶交换排序串行实现
     * exchFlag用于记录当前迭代是否发生了数据交换
     * start用于表示是奇交换还是偶交换
     * 初始时,start为0,表示进行偶交换,每次迭代结束后,切换start的状态
     * 如果上一次比较交换发生了数据交换,或者当前正在进行的是奇交换,循环就不会停止,
     * 直到程序不再发生交换,并且当前进行的是偶交换为止(表示奇偶交换已经成对出现)
     */
    public static void oddEvenSort(int[] arr) {
        int exchFlag = 1, start = 0;
        while (exchFlag == 1 || start == 1) {
            exchFlag = 0;
            for (int i = start; i < arr.length - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    exchFlag = 1;
                }
            }
            if (start == 0) {
                start = 1;
            } else {
                start = 0;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int[] array = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
//        oddEvenSort(array);
        pOddEventSort(array);
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
