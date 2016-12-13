package com.sun.sylvanas.utils.array;

/**
 * 数组工具类
 * <p>
 * Created by sylvanasp on 2016/12/13.
 */
public class ArrayUtils {

    /**
     * 用来表示排序类别的枚举类
     */
    public enum SortType {
        BUBBING, SHELL, ODD_EVEN, INSERT;
    }

    /**
     * 根据给出的value找出数组当中相匹配的值
     *
     * @param value 要搜索的值
     * @param arr   数组
     * @return 数组下标, 如果未找到则返回-1
     */
    public static int search(int value, int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("目标数组为null.");
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 排序的入口,根据排序枚举类分别调用对应的排序算法
     *
     * @param arr  数组
     * @param type 排序类别
     */
    public static void sort(int[] arr, SortType type) {
        if (arr == null) {
            throw new IllegalArgumentException("目标数组为null");
        }
        if (type == null) {
            throw new IllegalArgumentException("排序算法类型为null");
        }
        if (type.equals(SortType.BUBBING)) {
            bubbingSort(arr);
        } else if (type.equals(SortType.ODD_EVEN)) {
            oddEvenSort(arr);
        } else if (type.equals(SortType.INSERT)) {
            insertSort(arr);
        } else if (type.equals(SortType.SHELL)) {
            shellSort(arr);
        } else {
            System.out.println("找不到相应的排序算法.");
        }
    }

    /**
     * 冒泡排序
     */
    private static void bubbingSort(int[] arr) {
        System.out.println("execute bubbingSort.");
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
     * 奇偶数交换排序
     * exchFlag为交换标记
     * start为奇偶数标记
     */
    @SuppressWarnings("Duplicates")
    private static void oddEvenSort(int[] arr) {
        System.out.println("execute oddEvenSort.");
        int exchFlag = 1, start = 0;
        while (exchFlag == 1 || start == 1) {
            exchFlag = 0;
            for (int i = start; i < arr.length - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    exchFlag = 1;
                }
            }
            if (start == 0)
                start = 1;
            else
                start = 0;
        }
    }

    /**
     * 插入排序
     */
    private static void insertSort(int[] arr) {
        System.out.println("execute insertSort.");
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
     * 希尔排序
     */
    @SuppressWarnings("Duplicates")
    private static void shellSort(int[] arr) {
        System.out.println("execute shellSort.");
        int h = 1; //间隔数
        while (h <= arr.length / 3) {
            h = h * 3 + 1;
        }
        while (h > 0) {
            for (int i = h; i < arr.length; i++) {
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
            //计算出下一次间隔值
            h = (h - 1) / 3;
        }
    }

    public static void main(String[] args) {
        int[] arr = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        ArrayUtils.sort(arr, SortType.SHELL);
        System.out.println(ArrayUtils.search(3, arr));
    }

}
