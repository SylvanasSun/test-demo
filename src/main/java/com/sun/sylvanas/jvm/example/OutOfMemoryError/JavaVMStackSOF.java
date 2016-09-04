package com.sun.sylvanas.jvm.example.OutOfMemoryError;

/**
 * 在单线程中,以下两种方法均无法让虚拟机产生OutOfMemoryError异常,尝试的结果均为StackOverflowError异常.
 *
 * 1.使用-Xss参数减少栈内存容量.结果为StackOverflowError异常,出现时输出的堆栈深度相应缩小.
 *
 * VM Args: -Xss128k
 *
 * Created by sylvanasp on 2016/9/4.
 */
public class JavaVMStackSOF {

    private int stackLength = 1;

    /**
     * 定义大量的本地变量,增大此方法帧中本地变量表的长度.
     * 结果:抛出StackOverflowError异常时输出的堆栈深度相应缩小.
     */
    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) throws Throwable {
        /**
         * 在单线程下,无论是栈帧太大还是虚拟机栈容量太小,当内存无法分配时,
         * 虚拟机抛出的都是StackOverflowError异常.
         */
        JavaVMStackSOF javaVMStackSOF = new JavaVMStackSOF();
        try {
            javaVMStackSOF.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length:" + javaVMStackSOF.stackLength);
            throw e;
        }
    }

}
