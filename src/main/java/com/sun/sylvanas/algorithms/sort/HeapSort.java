package com.sun.sylvanas.algorithms.sort;

import com.sun.sylvanas.data_struct.heap.MaxHeap;
import com.sun.sylvanas.data_struct.heap.MinPriorityQueue;

import java.util.Scanner;

/**
 * Created by SylvanasSun on 2017/5/14.
 */
public class HeapSort extends BaseSort {

    private static boolean priorityQueueFlag = false; // 是否使用优先队列

    private HeapSort() {
    }

    public static void sort(Comparable[] a) {
        if (!priorityQueueFlag)
            maxHeapSort(a);
        else
            pqSort(a);
    }

    public static void usePriorityQueue(boolean flag) {
        priorityQueueFlag = flag;
    }

    // 使用优先队列实现堆排序
    private static void pqSort(Comparable[] a) {
        MinPriorityQueue<Comparable> priorityQueue = new MinPriorityQueue<>();
        for (int i = 0; i < a.length; i++) {
            priorityQueue.add(a[i]);
        }
        for (int i = 0; i < a.length; i++) {
            a[i] = priorityQueue.poll();
        }
    }

    // 使用最大堆实现堆排序
    private static void maxHeapSort(Comparable[] a) {
        MaxHeap<Comparable> maxHeap = new MaxHeap<>(a);
//        print(a);
        //不断地将最大堆中顶端元素(最大值)与最底部的元素(最小值)交换
        for (int i = a.length - 1; i > 0; i--) {
            Comparable largest = a[0];
            a[0] = a[i];
            a[i] = largest;
            // 堆减少,并调整新的堆
            maxHeap.heapify(a, i, 0);
        }
    }

    public static void main(String[] args) {
        System.out.println("Please input test data.");
        String[] s = new Scanner(System.in).nextLine().split("\\s+");
        HeapSort.usePriorityQueue(true);
        HeapSort.sort(s);
        HeapSort.print(s);
    }

}
