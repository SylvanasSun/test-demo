package com.sun.sylvanas.concurrent.algorithm.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 计算乘法
 * <p>
 * Created by sylvanasp on 2016/12/9.
 */
public class Multiply implements Runnable {
    public static BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

    public void run() {
        while (true) {
            try {
                Message message = queue.take();
                message.i = message.i * message.j;
                //将结果传递给除法线程
                Div.queue.add(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
