package com.sun.sylvanas.algorithms.sort;

/**
 * Created by SylvanasSun on 2017/5/15.
 */
public class QuickSort {

    private QuickSort() {
    }

    public static void sort(Comparable[] a) {
        sort(a, 0, a.length - 1);
    }

    private static void sort(Comparable[] a, int begin, int end) {
        if (begin >= end)
            return;

        int k = partitionUseEnd(a, begin, end);
        sort(a, begin, k - 1);
        sort(a, k + 1, end);
    }

    // 使用首元素作为基准值来进行切分
    private static int partitionUseBegin(Comparable[] a, int begin, int end) {
        Comparable pivot = a[begin];
        int i = begin;
        int j = end + 1;

        while (true) {
            // 从左向右扫描,直到找出一个大于等于基准的值
            while (less(a[++i], pivot)) {
                if (i >= end)
                    break;
            }

            // 从右向左扫描,直到找出一个小于等于基准的值
            while (less(pivot, a[--j])) {
                if (j <= begin)
                    break;
            }

            // 如果指针i与j发生碰撞则结束循环
            if (i >= j)
                break;
            // 将左边大于小于基准的值与右边小于等于基准的值进行交换
            swap(a, i, j);
        }
        // 将基准值交换到正确的位置上
        swap(a, begin, j);
        return j;
    }

    // 使用末尾元素作为基准值来进行切分
    private static int partitionUseEnd(Comparable[] a, int begin, int end) {
        Comparable pivot = a[end]; // 基准值,切分后的数组应满足左边都小于基准,右边都大于基准
        int i = begin - 1;

        for (int j = begin; j < end; j++) {
            // 如果j小于基准值则与i交换
            if (less(a[j], pivot)) {
                i++;
                swap(a, i, j);
            }
        }

        // 将基准值交换到正确的位置上
        int pivotLocation = i + 1;
        swap(a, pivotLocation, end);
        return pivotLocation;
    }

    // a < b ?
    private static boolean less(Comparable a, Comparable b) {
        return a.compareTo(b) < 0;
    }

    private static void swap(Object[] a, int i, int j) {
        Object t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void main(String[] args) {
        Integer[] a = {8,11,20,9,6,4,1,30,64,99,100,7};
        QuickSort.sort(a);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

}
