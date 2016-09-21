package com.sun.sylvanas.jvm.example.concurrent;

/**
 * 使用volatile变量的第二个语义是禁止指令重排序优化.
 * 普通的变量仅仅会保证在该方法的执行过程中所有依赖赋值结果的地方都能获取到正确的结果,
 * 而不能保证变量赋值操作的顺序与程序代码中的执行顺序一致.
 * 因为在一个线程的方法执行过程中无法感知到这点,这也就是Java内存模型中描述的所谓的
 * “线程内表现为串行的语义”(Within-Thread As-If-Serial Semantics).
 * <p>
 * Created by sylvanasp on 2016/9/21.
 */
public class Singleton {

    private volatile static Singleton instance;

    /**
     * 这是一段标准的DCL(双锁检测)单例代码.
     */
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        Singleton.getInstance();
    }

}
