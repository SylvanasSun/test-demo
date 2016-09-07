package com.sun.sylvanas.jvm.example;

/**
 * 线程死锁案例
 *
 * Created by sylvanasp on 2016/9/7.
 */
public class JStackDeadlockThreadTest {

    static class SynAddRunable implements Runnable {
        int a,b;

        public SynAddRunable(int a,int b) {
            this.a = a;
            this.b = b;
        }

        public void run() {
            synchronized (Integer.valueOf(a)) {
                synchronized (Integer.valueOf(b)) {
                    System.out.println(a + b);
                }
            }
        }
    }

    /**
     * 这段代码开了200个线程去分别计算 1+2 以及 2+1 的值.
     * 造成死锁的原因为Integer.valueOf()方法基于减少对象创建次数和节省内存的考虑.
     * [-128,127]之间的数会被缓存,当valueOf()方法传入参数在这个范围之间,将直接返回缓存中的对象.
     * 也就是说代码中调用了200次Integer.valueOf()方法其实一共就返回了两个不同的对象.
     * 假如某个线程的两个synchronized块之间发生了一次线程切换,那就会出现线程A等着被线程B持有的Integer.valueOf(1)
     * 线程B又等者被线程A持有的Integer.valueOf(2),结果两个线程会互相卡住,形成死锁.
     */
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(new SynAddRunable(1,2)).start();
            new Thread(new SynAddRunable(2,1)).start();
        }
    }

}
