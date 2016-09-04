package com.sun.sylvanas.jvm.example.OutOfMemoryError;

/**
 * 通过不断建立线程的方式产生内存溢出异常.
 * 但是这样产生的内存溢出异常与栈空间是否足够大并不存在任何联系.
 * 在这种情况下,为每个线程的栈分配的内存越大,反而越容易产生内存溢出异常.
 * 因为内存最后才由虚拟机栈和本地方法栈"瓜分".
 * 所以每个线程分配到的栈容量越大,可以建立的线程数量自然就越少,建立线程时就越容易把剩下的内存耗尽。
 *
 * 如果是建立过多线程导致的内存溢出,在不能减少线程数或者更换64位虚拟机的情况下.
 * 就只能通过减少最大堆和减少栈容量来换取更多的线程.
 *
 * VM Args: -Xss2M
 * <p>
 * Created by sylvanasp on 2016/9/4.
 */
public class JavaVMStackOOM {

    private void dontStop() {
        while (true) {

        }
    }

    public void stackLeakByThread() {
        while (true) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    dontStop();
                }
            });
            thread.start();
        }
    }

    public static void main(String[] args) {
        JavaVMStackOOM javaVMStackOOM = new JavaVMStackOOM();
        javaVMStackOOM.stackLeakByThread();
    }

}
