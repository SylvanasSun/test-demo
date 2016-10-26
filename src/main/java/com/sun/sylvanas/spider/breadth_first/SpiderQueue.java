package com.sun.sylvanas.spider.breadth_first;

/**
 * 宽度优先查找爬虫自定义队列类,封装了TODOQueue和VisitedQueue
 * <p>
 * Created by sylvanasp on 2016/10/26.
 */
public class SpiderQueue {

    // 未访问URL队列
    private final static TODOQueue unvisited = new TODOQueue();
    // 已访问URL队列
    private final static VisitedQueue visited = new VisitedQueue();

    /**
     * 添加URL到已访问队列
     */
    public static void addVisitedUrl(String url) {
        visited.addQueue(url);
    }

    /**
     * 移除访问过的URL
     */
    public static boolean removeVisitedUrl(String url) {
        return visited.removeQueue(url);
    }

    /**
     * 获得已访问的URL数量
     */
    public static int getVisitedUrlSize() {
        return visited.getQueueSize();
    }

    /**
     * 判断已访问URL队列是否为空
     */
    public static boolean visitedIsEmpty() {
        return visited.isQueueEmpty();
    }

    /**
     * 判断未访问URL队列是否为空
     */
    public static boolean unvisitedIsEmpty() {
        return unvisited.isQueueEmpty();
    }

    /**
     * 移除未访问URL队列中第一个元素,并返回
     */
    public static Object removeFirstUnvisitedUrl() {
        return unvisited.removeQueueFirst();
    }

    /**
     * 添加URL到未访问队列,需要判断该URL是否为第一次访问
     */
    public static void addUnvisitedUrl(String url) {
        if (url != null && !"".equals(url.trim())
                && !visited.contains(url)
                && !unvisited.contains(url)) {
            unvisited.addQueue(url);
        }
    }

    /**
     * 获得未访问URL队列
     */
    public static TODOQueue getUnvistedQueue() {
        return unvisited;
    }

    /**
     * 获得已访问URL队列
     */
    public static VisitedQueue getVisitedQueue() {
        return visited;
    }

}
