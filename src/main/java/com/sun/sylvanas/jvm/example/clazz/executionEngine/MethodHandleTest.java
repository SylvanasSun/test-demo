package com.sun.sylvanas.jvm.example.clazz.executionEngine;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * JSR-292 MethodHandle 基础用法案例.
 *
 * Created by sylvanasp on 2016/9/13.
 */
public class MethodHandleTest {

    static class ClassA {
        public void println(String s) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws Throwable {
        Object obj = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
        // 无论obj最终是哪个实现类,下面这句都正确调用到println方法
        /**
         * 实际上,getPrintlnMH()中模拟了invokevirtual指令的执行过程,
         * 只不过它的分派逻辑并非固化在Class文件的字节码上,而是通过一个具体方法来实现.
         * 而这个方法本身的返回值MethodHandle对象,可以视为对最终调用方法的一个"引用".
         */
        getPrintlnMH(obj).invokeExact("Hello World!");
    }

    private static MethodHandle getPrintlnMH(Object receiver) throws Throwable {
        /**
         * MethodType:代表"方法类型",包含了方法的返回值(methodType()的第一个参数)
         * 和具体参数(methodType()第二个及以后的参数).
         */
        MethodType mt = MethodType.methodType(void.class,String.class);
        /**
         * lookup()方法来自于MethodHandles.lookup,这句的作用是在指定类中查找符合给定的
         * 方法名称、方法类型,并且符合调用权限的方法句柄
         *
         * 因为这里调用的是一个虚方法,按照Java语言的规则,方法第一个参数是隐式的,代表该方法的接收者,
         * 也即是this指向的对象,这个参数以前是放在参数列表中进行传递的,而现在提供了bindTo()方法来完成这件事情.
         *
         */
        return lookup().findVirtual(receiver.getClass(),"println",mt).bindTo(receiver);
    }

}
