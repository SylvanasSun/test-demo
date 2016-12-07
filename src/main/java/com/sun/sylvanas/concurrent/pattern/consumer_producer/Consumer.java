package com.sun.sylvanas.concurrent.pattern.consumer_producer;

import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * 消费者线程,它从BlockingQueue队列中取出PCData对象,并进行相应的计算
 * <p>
 * Created by sylvanasp on 2016/12/7.
 */
public class Consumer implements Runnable {
    private BlockingQueue<PCData> queue; //缓冲区
    private static final int SLEEPTIME = 1000;

    public Consumer(BlockingQueue<PCData> queue) {
        this.queue = queue;
    }

    public void run() {
        System.out.println("start Consumer id=" + Thread.currentThread().getId());
        Random random = new Random();

        try {
            while (true) {
                PCData data = queue.take(); //提取出数据
                if (data != null) {
                    int re = data.getIntData() * data.getIntData(); //计算平方
                    System.out.println(MessageFormat.format("{0}*{1}={2}",
                            data.getIntData(), data.getIntData(), re));
                    Thread.sleep(random.nextInt(SLEEPTIME));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
