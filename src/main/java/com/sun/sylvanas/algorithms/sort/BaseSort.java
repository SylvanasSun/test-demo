package com.sun.sylvanas.algorithms.sort;

/**
 * Created by SylvanasSun on 2017/5/16.
 */
public class BaseSort {

    protected static Integer[] example = {8, 11, 20, 9, 6, 4, 1, 30, 64, 99, 100, 7};

    protected static void swap(Object[] a, int i, int j) {
        Object t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void print(Object[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

    protected static boolean less(Comparable a, Comparable b) {
        return a.compareTo(b) < 0;
    }

    protected static boolean greater(Comparable a, Comparable b) {
        return a.compareTo(b) > 0;
    }

}
