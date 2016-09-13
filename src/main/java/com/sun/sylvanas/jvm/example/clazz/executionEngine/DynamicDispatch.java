package com.sun.sylvanas.jvm.example.clazz.executionEngine;

/**
 * 动态分派案例
 * 动态分派的典型应用为方法重写.
 * Created by sylvanasp on 2016/9/13.
 */
public class DynamicDispatch {

    static abstract class Animal {
        protected abstract void sayHello();
    }

    static class Dog extends Animal {
        @Override
        protected void sayHello() {
            System.out.println("Dog say Hello");
        }
    }

    static class Cat extends Animal {
        @Override
        protected void sayHello() {
            System.out.println("Cat say Hello");
        }
    }

    /**
     * 运行结果为:
     * Dog say Hello
     * Cat say Hello
     * Cat say Hello
     *
     * 由于invokevirtual指令执行的第一步就是在运行期确定接收者(执行目标方法的所有者)的实际类型,
     * 所以两次调用中的invokevirtual指令把常量池中的类方法符号引用解析到了不同的直接引用上,
     * 这个过程就是Java语言中方法重写的本质.
     * 这种在运行期根据实际类型确定方法执行版本的分派过程称为动态分派.
     *
     */
    public static void main(String[] args) {
        Animal dog = new Dog();
        Animal cat = new Cat();
        dog.sayHello();
        cat.sayHello();
        dog = new Cat();
        dog.sayHello();
    }

}
