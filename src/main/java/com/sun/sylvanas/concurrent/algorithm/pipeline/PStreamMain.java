package com.sun.sylvanas.concurrent.algorithm.pipeline;

/**
 * 使用流水线的计算
 * 这种设计思路可以有效地将有依赖关系的操作分配在不同的线程中进行计算
 * 尽可能利用多核优势
 * <p>
 * Created by sylvanasp on 2016/12/9.
 */
public class PStreamMain {
    public static void main(String[] args) {
        new Thread(new Plus()).start();
        new Thread(new Multiply()).start();
        new Thread(new Div()).start();

        for (int i = 1; i <= 1000; i++) {
            for (int j = 1; j <= 1000; j++) {
                Message message = new Message();
                message.i = i;
                message.j = j;
                message.orgStr = "((" + i + "+" + j + ")*" + i + ")/2";
                Plus.queue.add(message);
            }
        }
    }
}
