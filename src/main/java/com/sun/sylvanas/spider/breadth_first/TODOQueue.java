package com.sun.sylvanas.spider.breadth_first;

import java.util.LinkedList;

/**
 * 宽度优先查找自定义队列,用于存储TODO(待访问)的URL
 * 使用LinkedList实现
 * <p>
 * Created by sylvanasp on 2016/10/26.
 */
public class TODOQueue implements Queue {

    private LinkedList<Object> queue = new LinkedList<Object>();

    /**
     * 添加o到队列末尾
     */
    public void addQueue(Object o) {
        queue.addLast(o);
    }

    public boolean removeQueue(Object o) {
        return queue.remove(o);
    }

    /**
     * 移除队列中的第一个元素并返回
     */
    public Object removeQueueFirst() {
        return queue.removeFirst();
    }

    /**
     * 判断元素o是否包含在队列中
     */
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    /**
     * 判断队列是否为空
     */
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

}
