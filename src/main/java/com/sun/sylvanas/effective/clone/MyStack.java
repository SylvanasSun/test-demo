package com.sun.sylvanas.effective.clone;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Created by sylvanasp on 2016/9/28.
 */
public class MyStack {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPAITY = 16;

    public MyStack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPAITY];
    }

    public void push(Object obj) {
        ensureCapacity();
        elements[size++] = obj;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null; // 消除废弃引用
        return result;
    }

    /**
     * 更新容器容量
     */
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }

    /**
     * 如果想要保证size域中的值也是正确的.
     * 可以在elements数组中递归地调用clone.
     */
    @Override
    public MyStack clone() {
        try {
            MyStack result = (MyStack) super.clone();
            result.elements = elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
