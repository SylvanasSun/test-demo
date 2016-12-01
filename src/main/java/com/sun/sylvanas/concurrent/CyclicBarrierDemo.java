package com.sun.sylvanas.concurrent;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 循环栅栏和CountDownLatch类似,它也可以实现线程间的计数等待,但它的功能更加复杂且强大.
 * CyclicBarrier是可以循环使用,它还可以接收一个参数作为barrierAction.
 * 即当计数器一次计数完成后,系统会执行的动作.
 * <p>
 * Created by sylvanasp on 2016/12/1.
 */
public class CyclicBarrierDemo {
    public static class Soldier implements Runnable {
        private String soldier;
        private final CyclicBarrier cyclicBarrier;

        public Soldier(CyclicBarrier cyclicBarrier, String soldierName) {
            this.cyclicBarrier = cyclicBarrier;
            this.soldier = soldierName;
        }

        public void run() {
            try {
                //等待所有士兵到齐
                cyclicBarrier.await();
                //模拟任务
                Thread.sleep(Math.abs(new Random().nextInt() % 10000));
                System.out.println(soldier + ":任务完成");
                //再一次调用,为了监控是否所有士兵完成任务
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public static class BarrierAction implements Runnable {
        boolean flag;
        int N;

        public BarrierAction(boolean flag, int N) {
            this.flag = flag;
            this.N = N;
        }

        public void run() {
            if (flag) {
                System.out.println("司令:[士兵" + N + "个,任务完成!]");
            } else {
                System.out.println("司令:[士兵" + N + "个,集合完毕!]");
                flag = true;
            }
        }
    }

    public static void main(String[] args) {
        final int N = 10;
        Thread[] allSoldier = new Thread[N];
        boolean flag = false;
        //将计数器设置为10,并要求计数完成后执行BarrierAction的run方法
        CyclicBarrier cyclicBarrier = new CyclicBarrier(N, new BarrierAction(flag, N));
        System.out.println("集合队伍!");
        for (int i = 0; i < N; i++) {
            System.out.println("士兵" + i + "报道!");
            allSoldier[i] = new Thread(new Soldier(cyclicBarrier, "士兵" + i));
            allSoldier[i].start();
        }
    }
}
