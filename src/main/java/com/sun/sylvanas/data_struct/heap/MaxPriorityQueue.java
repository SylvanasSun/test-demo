package com.sun.sylvanas.data_struct.heap;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by SylvanasSun on 2017/5/22.
 */
public class MaxPriorityQueue<T> implements Serializable, Iterable<T> {

    private static final long serialVersionUID = 1468745560119861496L;
    private static final int DEFAULT_CAPACITY = 16;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private Object[] queue;
    private int size = 0;

    /**
     * Created a priority queue with default capacity.
     */
    public MaxPriorityQueue() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Create a priority queue with given the specified capacity.
     *
     * @param capacity capacity
     * @throws if {@code capacity} less than 1
     */
    public MaxPriorityQueue(int capacity) {
        if (capacity < 1)
            throw new IllegalArgumentException();
        queue = new Object[capacity];
    }

    /**
     * This priority queue is empty?
     *
     * @return if {@code true} represent is empty,{@code false} otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Return the numbers represent queue size.
     *
     * @return the numbers represent queue size
     */
    public int size() {
        return size;
    }

    /**
     * Insert new elements into this priority queue.
     *
     * @param t new elements
     * @return if {@code true} represent insert success,{@code false} otherwise
     * @throws NullPointerException if {@code t} is null
     */
    public boolean add(T t) {
        if (t == null)
            throw new NullPointerException();
        if (size == queue.length)
            resize(queue.length * 2);
        int i = size;
        if (i == 0)
            queue[0] = t;
        else
            swim(i, t);
        size++;
        return true;
    }

    /**
     * Return this priority queue first element and remove it.
     * if this priority queue is empty return null.
     *
     * @return the this priority queue first element
     */
    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty())
            return null;
        int s = --size;
        Object result = queue[0];
        Object end = queue[s];
        queue[s] = null;
        if (s != 0)
            sink(0, (T) end);
        if (size <= queue.length / 4)
            resize(queue.length / 2);
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (size == 0) ? null : (T) queue[0];
    }

    @SuppressWarnings("unchecked")
    private void swim(int i, T t) {
        Comparable<? super T> key = (Comparable) t;
        while (i > 0) {
            int parent = (i - 1) >>> 1;
            T p = (T) queue[parent];
            if (key.compareTo(p) < 0)
                break;
            queue[i] = p;
            i = parent;
        }
        queue[i] = key;
    }

    @SuppressWarnings("unchecked")
    private void sink(int i, T t) {
        Comparable<? super T> key = (Comparable<? super T>) t;
        int half = size >>> 1;
        while (i < half) {
            int child = (i << 1) + 1;
            int right = child + 1;
            T max = (T) queue[child];
            // find maximum element
            if (right < size &&
                    ((Comparable<? super T>) max).compareTo((T) queue[right]) < 0)
                max = (T) queue[child = right];
            if (key.compareTo(max) > 0)
                break;
            queue[i] = max;
            i = child;
        }
        queue[i] = key;
    }

    // resize this priority queue with given specified new capacity
    private void resize(int newCapacity) {
        if (newCapacity < size)
            return;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(newCapacity);
        this.queue = Arrays.copyOf(queue, newCapacity);
    }

    private int hugeCapacity(int capacity) {
        if (capacity < 0)
            throw new OutOfMemoryError();
        return (capacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    @Override
    public Iterator<T> iterator() {
        return new MaxPriorityQueueIterator();
    }

    private class MaxPriorityQueueIterator implements Iterator<T> {
        private MaxPriorityQueue<T> copy;

        @SuppressWarnings("unchecked")
        public MaxPriorityQueueIterator() {
            this.copy = new MaxPriorityQueue<T>(size);
            for (int i = 0; i < size; i++) {
                copy.add((T) queue[i]);
            }
        }

        @Override
        public boolean hasNext() {
            return !copy.isEmpty();
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return copy.poll();
        }
    }

    public static void main(String[] args) {
        Integer[] arr = {100, 98, 60, 45, 10, 4, 2, 52, 41};
        MaxPriorityQueue<Integer> mq = new MaxPriorityQueue<>();
        Arrays.stream(arr).forEach(x -> {
            mq.add(x);
        });
        int i = 0;
        for (Integer a : mq) {
            arr[i++] = a;
        }
        Arrays.stream(arr).forEach(x -> {
            System.out.print(x + " ");
        });
        System.out.println();
    }

}
