package com.sun.sylvanas.algorithms.sort;

import java.nio.channels.Selector;

/**
 * 选择排序的思想是将索引第一位中的元素不断地与数组中其他元素比较交换,最终最小的元素会放入第一位中
 * (第二次循环时从第二位开始...以此类推)
 * Created by SylvanasSun on 2017/5/16.
 */
public class SelectSort extends BaseSort {

    private SelectSort() {
    }

    public static void sort(Comparable[] a) {
        for (int i = 0; i < a.length; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) {
                if (less(a[j], a[min]))
                    min = j;
            }
            swap(a, i, min);
        }
    }

    public static void main(String[] args) {
        Integer[] a = SelectSort.example;
        SelectSort.sort(a);
        SelectSort.print(a);
    }

}
