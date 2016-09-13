package com.sun.sylvanas.jvm.example.clazz.executionEngine;

/**
 * 静态分派案例
 *
 * 所有依赖静态类型来定位方法执行版本的分派动作称为静态分派.
 * 静态分派的典型应用是方法重载.
 * 静态分派发生在编译阶段,因此确定静态分派的动作实际上不是由虚拟机来执行的.
 * 但在很多情况下这个重载版本并不是唯一的,往往只能确定一个更加合适的版本.
 * 产生这种模糊结论的主要原因是字面量不需要定义,所以字面量没有显式的静态类型.
 * 它的静态类型只能通过语言上的规则去理解和推断.
 *
 * 例如: 如果输入一个字符'a',则会按照 char->int->long->float->double的顺序转型进行匹配.
 * 但不会匹配到byte和short类型的重载,因为char到byte或short的转型是不安全的.
 *
 * Created by sylvanasp on 2016/9/13.
 */
public class StaticDispatch {

    static abstract class Human {

    }

    static class Man extends Human {

    }

    static class Woman extends Human {

    }

    public void sayHello(Human guy) {
        System.out.println("Hello,guy!");
    }

    public void sayHello(Man guy) {
        System.out.println("Hello,gentleman!");
    }

    public void sayHello(Woman guy) {
        System.out.println("Hello,lady!");
    }

    /**
     * 运行结果为:
     * Hello,guy!
     * Hello,guy!
     *
     * 在方法接收者已经确定对象"sd"的前提下,使用哪个重载版本,完全取决于传入参数的数量和数据类型.
     * 但虚拟机(准确地说是编译器)在重载时是通过参数的静态类型而不是实际类型作为判定依据的.
     * 并且静态类型是编译期可知的,因此,在编译阶段,Javac编译器会根据参数的静态类型决定使用哪个重载版本.
     *
     */
    public static void main(String[] args) {
        /**
         * "Human"称为变量的静态类型,或者叫做外观类型.
         * 后面的"Man"则称为变量的实际类型.
         * 静态类型和实际类型在程序中都可以发生一些变化.
         * 区别是静态类型的变化仅仅在使用时发生,变量本身的静态类型不会被改变,并且最终的静态类型是在编译期可知的.
         * 实际类型变化的结果在运行期才可确定,编译器在编译程序的时候并不知道一个对象的实际类型是什么.
         *
         * 例如以下代码:
         * // 实际类型变化
         * Human man = new Man();
         * man = new Woman();
         * // 静态类型变化
         * sd.sayHello((Man) man);
         * sd.sayHello((Woman) man);
         */
        Human man = new Man();
        Human woman = new Woman();
        StaticDispatch sd = new StaticDispatch();
        sd.sayHello(man);
        sd.sayHello(woman);
    }

}
