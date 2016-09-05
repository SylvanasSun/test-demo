package com.sun.sylvanas.jvm.example.GC;

/**
 * 引用计数算法测试
 *
 * 引用计数算法即给对象中添加一个引用计数器,每当有一个地方引用它时,计数器值就加1.
 * 当引用失效时,计数器值就减1,任何时刻计数器为0的对象就是不可能再被使用的.
 *
 * Created by sylvanasp on 2016/9/5.
 */
public class ReferenceCountingGC {

    private Object instance = null;

    private static final int _1MB = 1024 * 1024;

    /**
     * 用于占点内存,以便能在GC日志中看清是否被回收过
     */
    private byte[] bigSize = new byte[2 * _1MB];

    /**
     * 对象objA和objB都有字段Instance.
     * 赋值令objA.instance = objB, objB.instance = objA.
     * 除此之外,没有任何引用.
     * 实际上objA和objB已经不可能再被访问,但是它们因为互相引用对方,导致引用计数都不为0.
     * 所以引用计数算法无法通知GC收集器回收它们.
     */
    private static void testGC() {
        ReferenceCountingGC objA = new ReferenceCountingGC();
        ReferenceCountingGC objB = new ReferenceCountingGC();
        objA.instance = objB;
        objB.instance = objA;

        objA = null;
        objB = null;

        System.gc();
    }

    public static void main(String[] args) {
        testGC();
    }

}
