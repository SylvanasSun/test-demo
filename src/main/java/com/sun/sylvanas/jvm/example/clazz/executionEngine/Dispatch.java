package com.sun.sylvanas.jvm.example.clazz.executionEngine;

/**
 * 单分派、多分派案例.
 *
 * 方法的接收者与方法的参数统称为方法的宗量.
 * 根据分派基于多少种宗量,可以将分派划分为单分派和多分派两种.
 * 单分派是根据一个宗量对目标方法进行选择,多分派则是根据多于一个宗量对目标方法进行选择.
 *
 * 今天的Java语言是一门静态多分派,动态单分派的语言.
 *
 * Created by sylvanasp on 2016/9/13.
 */
public class Dispatch {

    static class QQ {}

    static class _360 {}

    public static class Father {

        public void hardChoice(QQ arg) {
            System.out.println("father choose qq");
        }

        public void hardChoice(_360 arg) {
            System.out.println("father choose 360");
        }

    }

    public static class Son extends Father {

        public void hardChoice(QQ arg) {
            System.out.println("son choose qq");
        }

        public void hardChoice(_360 arg) {
            System.out.println("son choose 360");
        }

    }

    /**
     * 运行结果为:
     * father choose 360
     * son choose qq
     *
     * 在编译阶段编译器的选择过程中(静态分派的过程),这时选择目标方法的依据有两点:
     * 1.静态类型是Father还是Son.
     * 2.方法参数是QQ还是360.
     * 这次选择结果的最终产物是产生了两条invokevirtual指令,两条指令的参数分别为常量池中指向
     * Father.hardChoice(360)及Father.hardChoice(QQ)方法的符号引用.
     * 因为是根据两个宗量进行选择,所以Java语言的静态分派属于多分派类型.
     *
     * 在运行阶段虚拟机的选择中(动态分派的过程),在执行"son.hardChoice(new QQ())"这句代码时,
     * 即在执行这句代码所对应的invokevirtual指令时,由于编译期已经决定目标方法的签名必须为hardChoice(QQ),
     * 虚拟机此时不会关心传递过来的参数"QQ"到底是什么哪种“QQ”,因为这时参数的静态类型、实际类型都对方法的选择
     * 不会构成任何影响,唯一可以影响虚拟机选择的因素只有此方法的接收者的实际类型是Father还是Son.
     * 因为只有一个宗量作为选择依据,所以Java语言的动态分派属于单分派类型.
     *
     */
    public static void main(String[] args) {
        Father father = new Father();
        Father son = new Son();
        father.hardChoice(new _360());
        son.hardChoice(new QQ());
    }

}
