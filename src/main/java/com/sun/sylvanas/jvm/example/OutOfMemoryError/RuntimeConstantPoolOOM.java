package com.sun.sylvanas.jvm.example.OutOfMemoryError;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法区和运行时常量池溢出
 * 在JDK1.6及之前的版本中,由于常量池分配在永久代内.
 * 所以可以通过 -XX:PermSize和-XX:MaxPermSize限制方法区大小.
 * 从而间接限制其中常量池的容量.
 *
 * 而使用JDK1.7运行这段程序则不会得到相同的结果,while循环将一直进行下去.
 *
 * VM Args : -XX:PermSize=10M -XX:MaxPermSize=10M
 *
 * Created by sylvanasp on 2016/9/4.
 */
public class RuntimeConstantPoolOOM {

    public static void main(String[] args) {
        // 使用List保持常量池的引用,避免Full GC回收常量池行为.
        List<String> list = new ArrayList<String>();
        int i = 0;
        while (true) {
            /**
             * String.intern()是一个Native方法.
             * 它的作用是:
             * 如果字符串常量池中已经包含了一个等于此String对象的字符串,则返回代表池中这个字符串的String对象.
             * 否则,将此String对象包含的字符串添加到常量池中,并且返回此String对象的引用.
             */
            list.add(String.valueOf(i++).intern());
        }
    }

    /**
     * 这段代码在JDK1.6中运行,会得到两个false,而在JDK1.7中运行,会得到一个true和一个false.
     * 产生差异的原因为:
     * 在JDK1.6中,intern()方法会把首次遇到的字符串实例复制到永久代中,返回的也是永久代中这个字符串实例的引用.
     * 而由StringBuilder创建的字符串实例在Java堆上,所以必然不是同一个引用.所以返回false.
     *
     * 在JDK1.7中(或部分其他虚拟机,如JRockit),intern()实现不会再复制实例.
     * 只是在常量池中记录首次出现的实例引用,因此intern()返回的引用和由StringBuilder创建的字符串实例为同一个.
     * 对str2比较返回false是因为"java"这个字符串在执行StringBuilder.toString()之前已经出现过,
     * 字符串常量池中已经有它的引用了,不符合"首次出现"的原则.
     *
     */
    private void stringPool() {
        String str1 = new StringBuilder("Hello").append("World哈哈").toString();
        System.out.println(str1.intern() == str1);

        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern() == str2);
    }

}
