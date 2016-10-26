package com.sun.sylvanas.spider.breadth_first;

/**
 * 自定义队列公用接口
 * <p>
 * Created by sylvanasp on 2016/10/26.
 */
public interface Queue {

    void addQueue(Object o);

    boolean removeQueue(Object o);

    boolean contains(Object o);

    boolean isQueueEmpty();
}
