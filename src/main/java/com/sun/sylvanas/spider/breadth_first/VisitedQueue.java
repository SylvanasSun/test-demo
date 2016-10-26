package com.sun.sylvanas.spider.breadth_first;

import java.util.HashSet;
import java.util.Set;

/**
 * 宽度优先查找自定义队列,用于存储Visited(已访问)的URL
 * 这个数据结构需要不重复并且能快速查找，所以选择HashSet来存储
 * <p>
 * Created by sylvanasp on 2016/10/26.
 */
public class VisitedQueue implements Queue {

    private Set<Object> queue = new HashSet<Object>();

    public void addQueue(Object o) {
        queue.add(o);
    }

    public boolean removeQueue(Object o) {
        return queue.remove(o);
    }

    public boolean contains(Object o) {
        return queue.contains(o);
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * 获得已访问url的数量
     */
    public int getQueueSize() {
        return queue.size();
    }

}
