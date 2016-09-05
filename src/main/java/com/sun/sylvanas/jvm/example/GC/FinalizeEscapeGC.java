package com.sun.sylvanas.jvm.example.GC;

/**
 * 演示对象自救过程:
 * 对象可以通过finalize()方法完成自救.
 * 但是这种自救的机会只有一次,因为一个对象的finalize()方法最多只会被系统自动调用一次.
 *
 * 由于finalize()方法只是Java刚诞生时对C/C++程序员的一种妥协,它并不是C/C++中的析构函数.
 * 所以这种自救方法运行代价高昂,不稳定,不建议使用这种方式!!!
 *
 * Created by sylvanasp on 2016/9/5.
 */
public class FinalizeEscapeGC {

    public static FinalizeEscapeGC SAVE_HOOK = null;

    public void isAlive() {
        System.out.println("I am still alive ~");
    }

    /**
     * 重写finalize()方法.
     * 在不可达分析算法中要完成自救只要重新与引用链上的任何一个对象建立关联即可.
     * 例如:把自己(this)赋值给某个类变量或者对象的成员变量,那么在第二次标记时它将被移除出"即将回收"集合.
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize method executed!");
        FinalizeEscapeGC.SAVE_HOOK = this;
    }

    public static void main(String[] args) throws InterruptedException {
        SAVE_HOOK = new FinalizeEscapeGC();

        // 对象第一次成功自救
        SAVE_HOOK = null;
        System.gc();
        // 由于finalize方法优先级很低,所以需要暂停0.5秒以便等待它
        Thread.sleep(500);
        if(SAVE_HOOK != null) {
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("No! I am dead ~ QAQ");
        }

        // 这段代码与上面的相同,但由于finalize()方法只会被系统自动调用一次,所以自救失败.
        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(500);
        if(SAVE_HOOK != null) {
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("No! I am dead ~ QAQ");
        }
    }
}
