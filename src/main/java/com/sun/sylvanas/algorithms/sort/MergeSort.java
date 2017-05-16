package com.sun.sylvanas.algorithms.sort;

/**
 * Created by SylvanasSun on 2017/5/16.
 */
public class MergeSort extends BaseSort {

    private MergeSort() {
    }

    public static void sort(Comparable[] a) {
//        sort(a, 0, a.length - 1);
        sortUnRecursive(a);
    }

    // 非递归实现归并排序
    private static void sortUnRecursive(Comparable[] a) {
        int len = 1; // 自底向上实现归并排序,子序列的最小粒度为1
        while (len < a.length) {
            for (int i = 0; i < a.length; i += len * 2) {
                merge(a, i, len);
            }
            len *= 2; // 子序列规模每次迭代时乘2
        }
    }

    // 递归实现归并排序
    private static void sort(Comparable[] a, int lo, int hi) {
        if (lo >= hi)
            return;

        int mid = (lo + hi) >>> 1; // (lo + hi) / 2
        sort(a, lo, mid);
        sort(a, mid + 1, hi);
        merge(a, lo, mid, hi);
    }

    private static void merge(Comparable[] a, int lo, int hi) {
        Comparable[] aux = new Comparable[a.length];
        // 前半数组
        int i = lo;
        int i_len = lo + hi;
        // 后半数组
        int j = lo + hi;
        int j_len = j + hi;
        int count = 0;

        while (i < i_len && j < j_len && j < a.length) {
            if (less(a[i], a[j]))
                aux[count++] = a[i++];
            else
                aux[count++] = a[j++];
        }

        while (i < i_len && i < a.length) {
            aux[count++] = a[i++];
        }
        while (j < j_len && j < a.length) {
            aux[count++] = a[j++];
        }
        count = 0;
        int k = lo;
        while (k < j && k < a.length) {
            a[k++] = aux[count++];
        }
    }

    // 将两个子序列进行归并
    private static void merge(Comparable[] a, int lo, int mid, int hi) {
        Comparable[] aux = new Comparable[a.length]; // 辅助数组
        int i = lo, j = mid + 1;
        int count = lo;
        // 对[lo...mid] 与 [mid+1...hi] 两个子序列的首元素进行比较,将较小的元素放入辅助数组
        while (i <= mid && j <= hi) {
            if (less(a[i], a[j]))
                aux[count++] = a[i++];
            else
                aux[count++] = a[j++];
        }

        //将[lo...mid] 与 [mid+1...hi] 两个子序列中剩余的元素放入辅助数组
        while (i <= mid) {
            aux[count++] = a[i++];
        }
        while (j <= hi) {
            aux[count++] = a[j++];
        }

        // 将辅助数组中的元素复制到源数组中
        for (int k = lo; k <= hi; k++) {
            a[k] = aux[k];
        }
    }

    public static void main(String[] args) {
        Integer[] a = {8, 11, 20, 9, 6, 4, 1, 30, 64, 99, 100, 7};
        MergeSort.sort(a);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

}
