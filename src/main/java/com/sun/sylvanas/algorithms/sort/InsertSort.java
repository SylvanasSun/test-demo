package com.sun.sylvanas.algorithms.sort;

/**
 * 插入排序的思想是假设左边序列为一个有序序列,将当前索引指向的元素与左边序列从右向左进行比较,寻找到合适的插入位置.
 * Created by SylvanasSun on 2017/5/16.
 */
public class InsertSort extends BaseSort {

    private InsertSort() {
    }

    public static void sort(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            int j = i;
            Comparable pivot = a[i];
            while (j > 0 && less(pivot, a[j - 1])) {
                a[j] = a[j - 1];
                j--;
            }
            a[j] = pivot;
        }
    }

    public static void main(String[] args) {
        Integer[] a = InsertSort.example;
        InsertSort.sort(a);
        InsertSort.print(a);
    }

}
