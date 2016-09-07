package com.sun.sylvanas.jvm.example;

import java.util.Map;

/**
 *
 * 在JDK1.5中,java.lang.Thread类新增了一个getAllStackTraces()方法用于获取
 * 虚拟机中所有线程的StackTraceElement对象.可以完成jstack的大部分功能.
 *
 * Created by sylvanasp on 2016/9/7.
 */
public class StackTraces {

    public static void main(String[] args) {

        for(Map.Entry<Thread,StackTraceElement[]> stackTrace : Thread.getAllStackTraces().entrySet()) {
            Thread thread = stackTrace.getKey();
            StackTraceElement[] stack = stackTrace.getValue();
            if(thread.equals(Thread.currentThread())) {
                continue;
            }
            System.out.println("\n线程: " + thread.getName() + "\n");
            for(StackTraceElement element : stack) {
                System.out.println("\t"+element+"\n");
            }
        }

    }

}
