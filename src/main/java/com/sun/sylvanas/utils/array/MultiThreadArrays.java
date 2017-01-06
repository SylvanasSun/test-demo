package com.sun.sylvanas.utils.array;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 内部使用多线程实现的ArrayUtils.
 * <p>
 * Created by sylvanasp on 2016/12/21.
 */
public class MultiThreadArrays {
    private static Logger logger = Logger.getLogger(MultiThreadArrays.class); //log4j
    private static AtomicInteger searchResult = null; //多线程搜索结果集,默认为-1(未找到)
    private static AtomicInteger exchFlag = null; //奇偶数交换flag,默认为1
    private static final int SEARCH_THREAD_NUM; //多线程搜索的线程数
    //线程池
    private static ExecutorService threadPool =
            new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>()) {
                @Override
                protected void afterExecute(Runnable r, Throwable t) {
                    logger.debug("<DEBUG> threadPool afterExecute: " + r);
                }

                @Override
                protected void beforeExecute(Thread t, Runnable r) {
                    logger.debug("<DEBUG> threadPool beforeExecute: " + t);
                }

                @Override
                protected void terminated() {
                    logger.debug("<DEBUG> threadPool terminated.");
                }
            };

    //初始化成员变量
    static {
        searchResult = new AtomicInteger(-1);
        exchFlag = new AtomicInteger(1);
        SEARCH_THREAD_NUM = 4;
    }

    /**
     * 底层的搜索函数
     *
     * @param start 起始索引
     * @param end   结束索引
     * @param destV 目标值
     * @param arr   数组
     * @return 如果未找到则返回-1
     */
    private static int basicSearch(int start, int end, int destV, int[] arr) {
        for (int i = start; i < end; i++) {
            //判断其他线程是否已经找到结果
            if (searchResult.get() >= 0) {
                return searchResult.get();
            }
            //继续搜索
            if (arr[i] == destV) {
                //如果compareAndSet设置成功,证明其他线程没有找到目标索引,返回这个索引
                if (searchResult.compareAndSet(-1, i)) {
                    return searchResult.get();
                }
                return i;
            }
        }
        return -1;
    }

    /**
     * 搜索函数
     *
     * @param destV 目标值
     * @param arr   数组
     * @return 如果未找到则返回-1
     */
    public static int search(int destV, int[] arr) {
        //校验数组是否合法
        if (!validateArray(arr)) {
            logger.debug("<DEBUG> " + MultiThreadArrays.class.getName()
                    + " search arr param invalid.");
            return -1;
        }
        //根据线程数计算子任务数(步数)
        int step = arr.length / SEARCH_THREAD_NUM + 1;
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < arr.length; i += step) {
            int end = i + step;
            if (end > arr.length) end = arr.length;
            //开启搜索线程
            futures.add(threadPool.submit(new SearchThread(i, end, destV, arr)));
        }
        //遍历futures
        for (Future<Integer> f : futures) {
            try {
                if (f.get() >= 0) {
                    return f.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();

            }
        }
        return -1;
    }

    /**
     * 排序的公开入口,如果没有指定类型,则默认为奇偶数排序.
     *
     * @param arr      需要排序的数组
     * @param sortType 排序算法类型
     */
    public static void sort(int[] arr, SortType sortType) {
        if (sortType == null) {
            oddEvenSort(arr);
        }
        if (sortType == SortType.BUBBLING) {
            bubblingSort(arr);
        }
        if (sortType == SortType.INSERT) {
            insertSort(arr);
        }
        if (sortType == SortType.ODD_EVEN) {
            oddEvenSort(arr);
        }
        if (sortType == SortType.SHELL) {
            shellSort(arr);
        }
    }

    public static void sort(int[] arr) {
        oddEvenSort(arr);
    }

    /**
     * 执行搜索的线程
     */
    private static class SearchThread implements Callable<Integer> {
        private int start, end;
        private int destV;
        private int[] arr;

        private SearchThread(int start, int end, int destV, int[] arr) {
            this.start = start;
            this.end = end;
            this.destV = destV;
            this.arr = arr;
        }

        @Override
        public Integer call() throws Exception {
            Thread.currentThread().setName("SearchThread-" + new Random().nextInt(1001));
            return basicSearch(start, end, destV, arr);
        }
    }

    /**
     * 希尔排序,使用多线程实现
     *
     * @param arr 需要排序的数组
     */
    private static void shellSort(int[] arr) {
        if (!validateArray(arr)) {
            logger.debug("<DEBUG > " + MultiThreadArrays.class.getName()
                    + " shellSort arr param is invalid.");
            return;
        }
        int h = 1;
        //初始化间隔值
        while (h <= arr.length / 3) {
            h = h * 3 + 1;
        }
        CountDownLatch latch = null;
        while (h > 0) {
            //当间隔值大于等于4时,初始化CountDownLatch
            if (h >= 4) {
                latch = new CountDownLatch(arr.length - h);
            }
            for (int i = h; i < arr.length; i++) {
                //当间隔值大于等于4时,使用多线程执行排序
                if (h >= 4) {
                    threadPool.submit(new ShellSortThread(i, h, arr, latch));
                } else {
                    shellSwap(arr, h, i);
                }
            }
            //等待其他线程完成
            if (latch != null) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //重新计算间隔值
            h = (h - 1) / 3;
        }
    }

    @SuppressWarnings("Duplicates")
    private static void shellSwap(int[] arr, int h, int i) {
        if (arr[i] < arr[i - h]) {
            int temp = arr[i];
            int j = i - h;
            while (j >= 0 && arr[j] > temp) {
                arr[j + h] = arr[j];
                j -= h;
            }
            arr[j + h] = temp;
        }
    }

    private static class ShellSortThread implements Runnable {
        private int i, h;
        private int[] arr;
        private CountDownLatch latch;

        private ShellSortThread(int i, int h, int[] arr, CountDownLatch latch) {
            this.i = i;
            this.h = h;
            this.arr = arr;
            this.latch = latch;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("ShellSortThread-" + new Random().nextInt(1001));
            shellSwap(arr, h, i);
            latch.countDown();
        }
    }

    /**
     * 奇偶数交换排序,使用多线程实现.
     *
     * @param arr 需要排序的数组
     */
    private static void oddEvenSort(int[] arr) {
        if (!validateArray(arr)) {
            logger.debug("<DEBUG> " + MultiThreadArrays.class.getName()
                    + " oddEvenSort arr param is invalid.");
            return;
        }
        int start = 0;
        while (exchFlag.get() == 1 || start == 1) {
            exchFlag.compareAndSet(1, 0);
            //使用CountDownLatch控制线程数量,如果为偶数数组,并且start为1时则为arr.length/2-1个线程数
            CountDownLatch latch = new CountDownLatch
                    (arr.length / 2 - (arr.length % 2 == 0 ? start : 0));
            for (int i = start; i < arr.length - 1; i += 2) {
                threadPool.submit(new OddEvenSortThread(i, arr, latch));
            }
            //等待所有线程结束
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (start == 0)
                start = 1;
            else
                start = 0;
        }

    }

    private static class OddEvenSortThread implements Runnable {
        private int i;
        private int[] arr;
        private CountDownLatch latch;

        private OddEvenSortThread(int i, int[] arr, CountDownLatch latch) {
            this.i = i;
            this.arr = arr;
            this.latch = latch;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("OddEvenSortThread-" + new Random().nextInt(1001));
            if (arr[i] > arr[i + 1]) {
                int temp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = temp;
                exchFlag.compareAndSet(0, 1);
            }
            latch.countDown();
        }
    }

    /**
     * 冒泡排序,它不是多线程实现的.
     *
     * @param arr 需要排序的数组
     */
    @SuppressWarnings("Duplicates")
    private static void bubblingSort(int[] arr) {
        if (!validateArray(arr)) {
            logger.debug("<DEBUG> " + MultiThreadArrays.class.getName()
                    + " bubblingSort arr param is invalid.");
            return;
        }
        for (int i = arr.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    /**
     * 插入排序,它不是多线程实现的.
     *
     * @param arr 需要排序的数组
     */
    @SuppressWarnings("Duplicates")
    private static void insertSort(int[] arr) {
        if (!validateArray(arr)) {
            logger.debug("<DEBUG> " + MultiThreadArrays.class.getName()
                    + " insertSort arr param is invalid.");
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            int temp = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > temp) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = temp;
        }
    }


    /**
     * 校验数组是否合法
     *
     * @param arr 数组参数
     * @return 合法true, 不合法false
     */
    private static <T> boolean validateArray(T[] arr) {
        return arr != null && (arr.length > 0);
    }

    private static boolean validateArray(int[] arr) {
        return arr != null && (arr.length > 0);
    }

    public static void shutdown() {
        threadPool.shutdown();
    }

    public static void shutdownNow() {
        threadPool.shutdownNow();
    }

    public enum SortType {
        SHELL, ODD_EVEN, BUBBLING, INSERT;
    }

    public static void main(String[] args) {
        int[] arr = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        Arrays.stream(arr).forEach(System.out::print);
        System.out.println();
        MultiThreadArrays.sort(arr, SortType.SHELL);
        Arrays.stream(arr).forEach(System.out::print);
        System.out.println();
        int search = MultiThreadArrays.search(10, arr);
        System.out.println(search);
        MultiThreadArrays.shutdown();
    }
}
