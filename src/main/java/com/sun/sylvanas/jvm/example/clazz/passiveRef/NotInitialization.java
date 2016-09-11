package com.sun.sylvanas.jvm.example.clazz.passiveRef;

/**
 *
 * 被动初始化类案例
 *
 * 通过子类引用父类的静态字段,不会导致子类初始化.
 *
 * Created by sylvanasp on 2016/9/11.
 */
public class NotInitialization {

    /**
     * 以下代码运行后,只会输出"SuperClass init!",而子类没有进行初始化.
     * 这是因为对于静态字段,只有直接定义这个字段的类才会被初始化,
     * 因此通过其子类来引用父类中定义的静态字段,只会触发父类的初始化而不会触发子类的初始化.
     */
    public static void main(String[] args) {
        System.out.println(SubClass.value);
    }

    /**
     * 通过数组定义来引用类,不会触发此类的初始化.
     * 但是这段代码触发了另外一个名为 "[com.sun.sylvanas.jvm.example.clazz.passiveRef.SuperClass"
     * 的类的初始化阶段,它是一个由虚拟机自动生成的、直接继承于java.lang.Object的子类,创建动作由字节码指令newarray触发.
     * 这个类代表了一个元素类型为SuperClass的一维数组,数组中应有的属性和方法都实现在这个类里.
     * 这个类封装了数组元素的访问方法(准确地说,越界检查不是封装在数组元素访问的类中,而是封装在
     * 数组访问的xaload、xastore字节码指令中).
     */
    public void array() {
        SuperClass[] sca = new SuperClass[10];
    }

    /**
     * 常量在编译阶段会存入调用类的常量池中,本质上并没有直接引用到定义常量的类,因此不会触发变量的类的初始化.
     * ConstClass类中的常量HELLOWORLD,其实在编译阶段通过常量传播优化,已经将此常量存储到了调用类(NotInitialization)
     * 的常量池中,以后NotInitialization对常量ConstClass.HELLOWORLD的引用实际都被转化为NotInitialization
     * 类对自身常量池的引用了.
     * 实际上,NotInitialization的Class文件中并没有ConstClass类的符号引用,这两个类在编译成Class之后不存在任何联系.
     */
    public void constTest() {
        System.out.println(ConstClass.HELLOWORLD);
    }

}
