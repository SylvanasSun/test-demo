package com.sun.sylvanas.data_struct.heap;

/**
 * 最大堆的简单实现
 * <p>
 * Created by SylvanasSun on 2017/5/14.
 */
public class MaxHeap<T extends Comparable> {

    T[] heap;

    private MaxHeap() {
    }

    public MaxHeap(T[] heap) {
        this.heap = heap;
        buildHeap();
    }

    /**
     * 自底向上构建堆
     */
    private void buildHeap() {
        int length = heap.length;
        // 当堆为空或者长度为1时不需要任何操作
        if (length <= 1)
            return;

        int root = (length - 2) >>> 1; // (i - 1) / 2
        while (root >= 0) {
            heapify(heap, length, root);
            root--;
        }
    }

    /**
     * 调整堆的结构
     *
     * @param heap   堆
     * @param length 堆的长度
     * @param root   根节点索引
     */
    public void heapify(T[] heap, int length, int root) {
        if (root >= length)
            return;

        int largest = root; // 表示root,left,right中最大值的变量
        int left = (root << 1) + 1; // 左子节点,root * 2 + 1
        int right = left + 1; // 右子节点,root * 2 + 2

        // 找出最大值
        if (left < length && greater(heap[left], heap[largest]))
            largest = left;
        if (right < length && greater(heap[right], heap[largest]))
            largest = right;

        // 如果largest发生变化,将largest与root交换
        if (largest != root) {
            T t = heap[root];
            heap[root] = heap[largest];
            heap[largest] = t;
            // 继续向下调整堆
            heapify(heap, length, largest);
        }
    }

    private boolean greater(Comparable a, Comparable b) {
        return a.compareTo(b) > 0;
    }

}
