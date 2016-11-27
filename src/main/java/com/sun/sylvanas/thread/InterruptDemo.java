package com.sun.sylvanas.thread;

/**
 * 线程中断处理
 * <p>
 * Created by sylvanasp on 2016/11/27.
 */
public class InterruptDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Interrupted!");
                        break;
                    }
                    /**
                     * Thread.sleep()方法由于中断而抛出异常,此时它会清除中断标记,
                     * 为了能在下一次循环中捕获这个中断,需要在异常处理中再次设置中断标记位.
                     */
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted When Sleep");
                        //设置中断标记位
                        Thread.currentThread().interrupt();
                    }
                    Thread.yield();
                }
            }
        };
        t1.start();
        Thread.sleep(2000);
        t1.interrupt();
    }

}
