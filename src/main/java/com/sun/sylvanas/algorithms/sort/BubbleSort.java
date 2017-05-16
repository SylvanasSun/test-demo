package com.sun.sylvanas.algorithms.sort;

/**
 * 冒泡排序的思想是相邻元素两两比较,将大的元素不断放到后面
 * Created by SylvanasSun on 2017/5/16.
 */
public class BubbleSort extends BaseSort {

    private BubbleSort() {
    }

    public static void sort(Comparable[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (less(a[j + 1], a[j])) {
                    swap(a, j, j + 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        Integer[] a = BubbleSort.example;
        BubbleSort.sort(a);
        BubbleSort.print(a);
    }

}
