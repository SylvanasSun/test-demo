package com.sun.sylvanas.data_struct;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by SylvanasSun on 2017/5/20.
 */
public class MinPriorityQueue<T> implements Serializable, Iterable<T> {

    private static final long serialVersionUID = 1670314569319288204L;

    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Represented as a binary heap.
     * the two children of queue[n] ara queue[2*n+1] and queue[2*n+2].
     * the parent of queue[n] ara queue[(n-1)/2].
     */
    private transient Object[] queue;

    /**
     * The number of elements in the priority queue.
     */
    private int size = 0;

    /**
     * Creates a {@code MinPriorityQueue} with the default
     * initial capacity.
     */
    public MinPriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a {@code MinPriorityQueue} with the given specified
     * initial capacity.
     *
     * @param initialCapacity the initial capacity for this priority queue
     * @throws IllegalArgumentException if {@code initialCapacity} is less than 1
     */
    public MinPriorityQueue(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.queue = new Object[initialCapacity];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @return {@code true} insert success,{@code false} otherwise
     * @throws NullPointerException if {@code t} is null
     */
    public boolean add(T t) {
        if (t == null)
            throw new NullPointerException();
        int i = size;
        if (i >= queue.length)
            resize(queue.length * 2);
        size = i + 1;
        if (i == 0)
            queue[0] = t;
        else
            swim(i, t);
        return true;
    }

    public T peek() {
        //noinspection unchecked
        return (size == 0) ? null : (T) queue[0];
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        if (size == 0)
            return null;
        int s = --size;
        T result = (T) queue[0];
        T end = (T) queue[s];
        queue[s] = null;
        if (s != 0)
            sink(0, end);
        if (size <= queue.length / 4)
            resize(queue.length / 2);
        return result;
    }

    @SuppressWarnings("unchecked")
    private void sink(int k, T t) {
        Comparable<? super T> key = (Comparable<? super T>) t;
        int half = size >>> 1; // loop while a non-leaf
        while (k < half) {
            int child = (k << 1) + 1;
            int right = child + 1;
            Object min = queue[child];
            // find minimum element
            if (right < size &&
                    ((Comparable<? super T>) min).compareTo((T) queue[right]) > 0)
                min = queue[child = right];
            if (key.compareTo((T) min) <= 0)
                break;
            queue[k] = min;
            k = child;
        }
        queue[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void swim(int k, T t) {
        Comparable<? super T> key = (Comparable<? super T>) t;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object p = queue[parent];
            if (key.compareTo((T) p) >= 0)
                break;
            queue[k] = p;
            k = parent;
        }
        queue[k] = key;
    }

    /**
     * Resize the capacity of the array.
     *
     * @param newCapacity the new capacity
     */
    private void resize(int newCapacity) {
        if (newCapacity <= size)
            return;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(newCapacity);
        this.queue = Arrays.copyOf(queue, newCapacity);
    }

    private static int hugeCapacity(int capacity) {
        if (capacity < 0)
            throw new OutOfMemoryError();
        return (capacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    @Override
    public Iterator<T> iterator() {
        return new MinPriorityQueueIterator();
    }

    private class MinPriorityQueueIterator implements Iterator<T> {
        private MinPriorityQueue<T> mq;

        MinPriorityQueueIterator() {
            mq = new MinPriorityQueue<T>(size);
            for (int i = 0; i < size; i++) {
                mq.add((T) queue[i]);
            }
        }

        @Override
        public boolean hasNext() {
            return !mq.isEmpty();
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return mq.poll();
        }
    }
}
