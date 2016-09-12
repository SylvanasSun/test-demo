package com.sun.sylvanas.jvm.example.clazz.loading;

/**
 *
 * 字段解析案例.
 *
 * 如果有一个同名字段同时出现在C的接口和父类中,或者同时在自己或父类的多个接口中出现,
 * 则编译器将可能拒绝编译.
 *
 * Created by sylvanasp on 2016/9/12.
 */
public class FieldResolution {

    interface Interface0 {
        int A  = 0;
    }

    interface Interface1 extends Interface0 {
        int A = 1;
    }

    interface Interface2 {
        int A = 2;
    }

    static class Parent implements Interface1 {
        public static int A = 3;
    }

    static class Sub extends Parent implements Interface2 {
        public static int A = 4;
    }

    /**
     * 因为Sub类中的 "public static int A=4",在接口与父类中同时存在字段A,
     * 所以编译器将提示"The field Sub.A is ambiguous",并且拒绝编译这段代码.
     */
    public static void main(String[] args) {
        System.out.println(Sub.A);
    }

}
