package com.sun.sylvanas.thread;

/**
 * join()方法的使用
 * <p>
 * Created by sylvanasp on 2016/11/27.
 */
public class JoinDemo {
    public volatile static int i = 0;

    public static class AddThread extends Thread {
        @Override
        public void run() {
            for (i = 0; i < 10000000; i++) ;
        }
    }

    /**
     * 使用join方法后,表示主线程愿意等待AddThread执行完毕,
     * 故在join返回时,AddThread已经是执行完毕的了,则输出的i总是10000000
     */
    public static void main(String[] args) throws InterruptedException {
        AddThread at = new AddThread();
        at.start();
        at.join();
        System.out.println(i);
    }
}
