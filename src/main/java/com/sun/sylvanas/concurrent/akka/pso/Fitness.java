package com.sun.sylvanas.concurrent.akka.pso;

import java.util.List;

/**
 * fitness()函数返回了给定投资方案的适应度.
 * 适应度也就是投资的收益,在这里适应度=√x1+√x2+√x3+√x4.
 * <p>
 * Created by sylvanasp on 2016/12/20.
 */
public class Fitness {
    public static double fitness(List<Double> x) {
        double sum = 0;
        for (int i = 1; i < x.size(); i++) {
            //+=每年投资额的开方.
            sum += Math.sqrt(x.get(i));
        }
        return sum;
    }
}
