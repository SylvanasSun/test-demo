package com.sun.sylvanas.utils.array;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 内部使用多线程实现的ArrayUtils.
 * <p>
 * Created by sylvanasp on 2016/12/21.
 */
public class MultiThreadArrays {
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();//线程池
    private static AtomicInteger searchResult = new AtomicInteger(-1);//搜索的result
    private static AtomicInteger exchFlag = new AtomicInteger(1);//奇偶数交换算法所需要的数据交换标记.
    private static final int SEARCH_THREAD_NUM = 4;//搜索时所需要的线程数

    /**
     * 底层的搜索函数,每个线程都要执行这个函数进行搜索.
     */
    private static int baseSearch(int searchValue, int begin, int end, int[] arr) {
        for (int i = begin; i < end; i++) {
            //判断其他线程是否已经找到目标,如果找到则立即返回
            if (searchResult.get() >= 0) {
                return searchResult.get();
            }
            //继续搜索
            if (arr[i] == searchValue) {
                //尝试CAS操作,如果成功则代表其他线程修改了结果集(找到目标)
                if (!searchResult.compareAndSet(-1, i)) {
                    if (searchResult.get() >= 0)
                        return searchResult.get();
                }
                return i;
            }
        }
        return -1;
    }

    private static class SearchThread implements Callable<Integer> {
        private int begin, end, searchValue;
        private int[] arr;

        public SearchThread(int searchValue, int begin, int end, int[] arr) {
            this.searchValue = searchValue;
            this.begin = begin;
            this.end = end;
            this.arr = arr;
        }

        @Override
        public Integer call() throws Exception {
            return baseSearch(searchValue, begin, end, arr);
        }
    }

    private static class OddEvenSortThread implements Runnable {
        private int i;
        private int[] arr;
        private CountDownLatch latch;

        public OddEvenSortThread(int i, int[] arr, CountDownLatch latch) {
            this.i = i;
            this.arr = arr;
            this.latch = latch;
        }

        @Override
        public void run() {
            if (arr[i] > arr[i + 1]) {
                int temp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = temp;
                exchFlag.compareAndSet(0, 1);
            }
            latch.countDown();
        }
    }

    private static class ShellSortThread implements Runnable {
        private int h, i;
        private int[] arr;
        private CountDownLatch latch;

        public ShellSortThread(int i, int h, int[] arr, CountDownLatch latch) {
            this.i = i;
            this.h = h;
            this.arr = arr;
            this.latch = latch;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public void run() {
            if (arr[i] < arr[i - h]) {
                int temp = arr[i];
                int j = i - h;
                while (j >= 0 && arr[j] > temp) {
                    arr[j + h] = arr[j];
                    j -= h;
                }
                arr[j + h] = temp;
            }
            latch.countDown();
        }
    }

    private static void oddEvenSort(int[] arr) throws InterruptedException {
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
            latch.await();
            if (start == 0)
                start = 1;
            else
                start = 0;
        }
    }

    @SuppressWarnings("Duplicates")
    private static void shellSort(int[] arr) throws InterruptedException {
        //计算间隔值(h)
        int h = 1;
        while (h <= arr.length / 3) {
            h = h * 3 + 1;
        }
        CountDownLatch latch = null;
        //只有当间隔值大于等于4时,再去分配多个线程进行排序.
        while (h > 0) {
            if (h >= 4) {
                latch = new CountDownLatch(arr.length - h);
            }
            for (int i = h; i < arr.length; i++) {
                if (h >= 4) {
                    threadPool.execute(new ShellSortThread(i, h, arr, latch));
                } else {
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
            }
            if (latch != null)
                latch.await();
            //重新计算间隔值
            h = (h - 1) / 3;
        }
    }

    public static void sort(int[] arr, SortType sortType) throws InterruptedException {
        if (arr == null || arr.length < 0) {
            throw new IllegalArgumentException("array is null or length less than 0.");
        }
        if (sortType == null) {
            oddEvenSort(arr);
        }
        if (sortType == SortType.ODD_EVEN) {
            oddEvenSort(arr);
        }
        if (sortType == SortType.SHELL) {
            shellSort(arr);
        }
        oddEvenSort(arr);
    }

    public static int search(int searchValue, int[] arr) throws ExecutionException, InterruptedException {
        if (arr == null || arr.length < 0) {
            throw new IllegalArgumentException("array is null or length less than 0.");
        }
        List<Future<Integer>> futureList = new ArrayList<>();
        int subArraySize = arr.length / SEARCH_THREAD_NUM + 1;//子数组长度
        for (int i = 0; i < arr.length; i += subArraySize) {
            int end = i + subArraySize;
            if (end > arr.length) end = arr.length;
            futureList.add(threadPool.submit(new SearchThread(searchValue, i, end, arr)));
        }
        //遍历futureList判断是否获得结果
        for (Future f : futureList) {
            int result = (int) f.get();
            if (result >= 0) {
                return result;
            }
        }
        return -1;
    }

    public static void shutdown() {
        threadPool.shutdown();
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[] array = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        MultiThreadArrays.sort(array, SortType.SHELL);
        int search = MultiThreadArrays.search(1, array);
        System.out.println(search);
    }
}
