package com.sun.sylvanas.effective.compositive;

import java.util.Collection;
import java.util.Set;

/**
 * CountSet,继承于包装类ForwardSet.
 * 这个类的作用为当添加元素时会记录添加的元素数量.
 * <p>
 * Created by sylvanasp on 2016/9/30.
 */
public class CountSet<E> extends ForwardSet<E> {

    private int count = 0;

    public CountSet(Set<E> set) {
        super(set);
    }

    @Override
    public boolean add(E e) {
        count++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        count += c.size();
        return super.addAll(c);
    }

    public int getCount() {
        return count;
    }
}
