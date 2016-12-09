package com.sun.sylvanas.concurrent.algorithm.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 计算除法
 * <p>
 * Created by sylvanasp on 2016/12/9.
 */
public class Div implements Runnable {
    public static BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

    public void run() {
        while (true) {
            try {
                Message message = queue.take();
                message.i = message.i / 2;
                System.out.println(message.orgStr + "=" + message.i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
