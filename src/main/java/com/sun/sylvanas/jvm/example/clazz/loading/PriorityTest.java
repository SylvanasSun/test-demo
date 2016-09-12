package com.sun.sylvanas.jvm.example.clazz.loading;

/**
 * 由于父类的<clinit>()方法先执行,父类中定义的静态语句块要优先于子类的变量赋值操作.
 *
 * Created by sylvanasp on 2016/9/12.
 */
public class PriorityTest {

    static class Parent {
        public static int A = 1;
        static {
            A = 2;
        }
    }

    static class Sub extends Parent {
        public static int B = A;
    }

    /**
     * 字段B的值将会是2而不是1.
     */
    public static void main(String[] args) {
        System.out.println(Sub.B);
    }

}
