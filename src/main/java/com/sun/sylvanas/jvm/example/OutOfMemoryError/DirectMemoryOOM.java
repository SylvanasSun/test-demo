package com.sun.sylvanas.jvm.example.OutOfMemoryError;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 直接内存溢出
 * DirectMemory容量可以通过 -XX:MaxDirectMemorySize指定.
 * 如果不指定,则默认与Java堆最大值(-Xmx)一致.
 *
 * VM Args : -Xmx20M -XX:MaxDirectMemorySize=10M
 *
 * Created by sylvanasp on 2016/9/4.
 */
public class DirectMemoryOOM {

    private static final int _1MB = 1024 * 1024;

    /**
     * 越过DirectByteBuffer类,直接通过反射获取Unsafe实例进行内存分配.
     * Unsafe.getUnsafe()方法限制了只有引导类加载器才会返回实例,
     * 即只有rt.jar中的类才能使用Unsafe的功能.
     *
     * 使用DirectByteBuffer类分配内存虽然也会抛出内存溢出的异常,
     * 但它抛出异常时并没有真正的向操作系统申请分配内存,而是通过计算得知内存无法分配,于是手动抛出异常.
     *
     * 而unsafe.allocateMemory()则可以真正申请分配内存.
     *
     */
    public static void main(String[] args) throws IllegalAccessException {

        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true) {
            unsafe.allocateMemory(_1MB);
        }
    }

}
