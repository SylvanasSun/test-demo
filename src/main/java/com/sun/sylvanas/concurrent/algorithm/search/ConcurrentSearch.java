package com.sun.sylvanas.concurrent.algorithm.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将原始数据集合按照期望的线程数进行分割
 * 每个线程各自独立搜索,当其中有一个线程找到数据后,立即返回结果即可.
 * <p>
 * Created by sylvanasp on 2016/12/9.
 */
public class ConcurrentSearch {
    static int[] arr; //需要查找的数组元素
    static ExecutorService pool = Executors.newCachedThreadPool();//线程池
    static final int THREAD_NUM = 4;//线程数量
    static AtomicInteger result = new AtomicInteger(-1);//结果(元素下标),默认为-1表示没有找到指定元素.

    public static int search(int searchValue, int beginPos, int endPos) {
        int i = 0;
        for (i = beginPos; i < endPos; i++) {
            //判断result是否已经有其他线程找到了需要的结果,如果有则立即返回
            if (result.get() >= 0) {
                return result.get();
            }
            //继续搜索
            if (arr[i] == searchValue) {
                //如果设置失败,表示其他线程已经先找到了
                if (!result.compareAndSet(-1, i)) {
                    return result.get();
                }
                return i;
            }
        }
        return -1;
    }

    /**
     * 搜索的线程
     */
    public static class SearchTask implements Callable<Integer> {
        int begin, end, searchValue;

        public SearchTask(int searchValue, int begin, int end) {
            this.begin = begin;
            this.searchValue = searchValue;
            this.end = end;
        }

        public Integer call() throws Exception {
            int re = search(searchValue, begin, end);
            return re;
        }
    }

    /**
     * 并行查找函数
     */
    public static int pSearch(int searchValue, int[] array) throws ExecutionException, InterruptedException {
        arr = array;
        int subArrSize = arr.length / THREAD_NUM + 1;
        List<Future<Integer>> re = new ArrayList<Future<Integer>>();
        //将数组分为若干段,并根据划分结果建立子任务
        for (int i = 0; i < arr.length; i += subArrSize) {
            int end = i + subArrSize;
            if (end >= arr.length) {
                end = arr.length;
            }
            re.add(pool.submit(new SearchTask(searchValue, i, end)));
        }
        for (Future<Integer> fu : re) {
            if (fu.get() >= 0) {
                return fu.get();
            }
        }
        return -1;
    }

    public static void shutdown() {
        pool.shutdown();
        System.out.println("线程池已经关闭!");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int result = ConcurrentSearch.pSearch(2, array);
        System.out.println("result:" + result);
        ConcurrentSearch.shutdown();
    }
}
