package com.sun.sylvanas.concurrent.algorithm.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 计算加法 即(b+c)*b/2中的 (b+c)
 * <p>
 * Created by sylvanasp on 2016/12/9.
 */
public class Plus implements Runnable {
    public static BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

    public void run() {
        while (true) {
            try {
                Message message = queue.take();
                message.j = message.i + message.j;
                //将结果传递给乘法线程,当没数据处理时,Plus进行等待(死循环)
                Multiply.queue.add(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
