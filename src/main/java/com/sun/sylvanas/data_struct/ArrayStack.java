package com.sun.sylvanas.data_struct;

import java.util.Iterator;

/**
 * Uses array implements a simple stack.
 * <p>
 * Created by SylvanasSun on 2017/4/23.
 *
 * @author SylvanasSun
 */
public class ArrayStack<T> implements Iterable<T> {

    private int N; // the number of this stack element size
    private final static int DEFAULT_CAPACITY = 8;
    private final static String CLASS_NAME = ArrayStack.class.getName();
    private T[] table;

    /**
     * Initialize a empty stack use default capacity(8).
     */
    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Initialize a empty stack use given capacity.
     *
     * @param capacity the capacity
     */
    public ArrayStack(int capacity) {
        table = (T[]) new Object[capacity];
    }

    /**
     * This stack is empty?
     *
     * @return {@code true} this stack is empty,{@code false} otherwise
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * Return the number of this stack element size.
     *
     * @return the number of this stack element size
     */
    public int size() {
        return N;
    }

    /**
     * Push element to this stack.
     *
     * @param t the element
     */
    public void push(T t) {
        if (t == null)
            throw new IllegalArgumentException(CLASS_NAME + " called push() argument is null.");
        if (N == table.length)
            resize(table.length * 2);
        table[N++] = t;
    }

    /**
     * Return stack top element(no remove it).
     *
     * @return the stack top element
     */
    public T peek() {
        if (isEmpty())
            return null;
        int temp = N;
        return table[--temp];
    }

    /**
     * Return stack top element and remove it.
     *
     * @return the stack top element
     */
    public T pop() {
        if (isEmpty())
            return null;
        T t = table[--N];
        table[N] = null;
        if (N > 0 && N == table.length / 4)
            resize(table.length / 2);
        return t;
    }

    /**
     * Resize this stack use given capacity.
     *
     * @param capacity the capacity
     */
    private void resize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        for (int i = 0; i < N; i++) {
            temp[i] = table[i];
        }
        this.table = temp;
    }

    private class ArrayStackIterator implements Iterator<T> {
        int i = N;

        @Override
        public boolean hasNext() {
            return i > 0;
        }

        @Override
        public T next() {
            return table[--i];
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayStackIterator();
    }

}
