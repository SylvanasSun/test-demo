package com.sun.sylvanas.concurrent.akka.pso;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 基本粒子
 * <p>
 * Created by sylvanasp on 2016/12/20.
 */
public class Particle extends UntypedActor {
    private PsoValue pBest = null;//个体最优
    private PsoValue gBest = null;//全局最优
    private List<Double> velocity = new ArrayList<Double>(5);//粒子在各维度上的速度,本例中每一年投资额可以为一个维度.
    private List<Double> x = new ArrayList<Double>(5);//每一年投资额
    private Random random = new Random();

    /**
     * 粒子初始化时,随机生成一组满足基本约束条件的投资组合,
     * 并计算出它的适应度,初始的投资方案自然也就作为当前的个体最优解,发送给Master.
     */
    @Override
    public void preStart() throws Exception {
        //初始化粒子的当前位置
        for (int i = 0; i < 5; i++) {
            velocity.add(Double.NEGATIVE_INFINITY);
            x.add(Double.NEGATIVE_INFINITY);
        }
        //x1<=400(第一年投资额不能超过400)
        x.set(1, (double) random.nextInt(401));

        //x2<=440-1.1*x1(第二年上限为440,假设第一年全部存银行,以此类推)
        double max = 400 - 1.1 * x.get(1);
        if (max <= 0) max = 0;
        x.set(2, random.nextDouble() * max);

        //x3<=484-1.21*x1-1.1*x2
        max = 484 - 1.21 * x.get(1) - 1.1 * x.get(2);
        if (max <= 0) max = 0;
        x.set(3, random.nextDouble() * max);

        //x4<=532.4 - 1.331 * x1 - 1.21 * x2 - 1.1 * x3
        max = 532.4 - 1.331 * x.get(1) - 1.21 * x.get(2) - 1.1 * x.get(3);
        if (max <= 0) max = 0;
        x.set(4, random.nextDouble() * max);

        double newFit = Fitness.fitness(x); //计算出适应度
        //将个体最优解发送给Master
        pBest = new PsoValue(newFit, x);
        PbestMsg pbestMsg = new PbestMsg(pBest);
        ActorSelection selection = getContext().actorSelection("/user/masterParticle");
        selection.tell(pbestMsg, getSelf());
    }

    /**
     * 当Master计算出当前全局最优解后,会将全局最优解发送给每一个粒子,
     * 粒子根据全局最优解更新自己的运行速度,并更新自己的速度以及当前位置.
     */
    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof GbestMsg) {
            gBest = ((GbestMsg) msg).getValue();
            //更新速度
            for (int i = 1; i < velocity.size(); i++) {
                updateVelocity(i);
            }
            //更新位置
            for (int i = 1; i < x.size(); i++) {
                updateX(i);
            }
            //检查约束范围
            validateX();
            //计算新位置的适应度,如果产生了新的个体最优解,则发送给Master.
            double newFit = Fitness.fitness(x);
            if (newFit > pBest.getValue()) {
                pBest = new PsoValue(newFit, x);
                PbestMsg pbestMsg = new PbestMsg(pBest);
                getSender().tell(pbestMsg, getSelf());
            }
        } else {
            unhandled(msg);
        }
    }

    //速度和位置的更新是依据标准的粒子群算法实现
    private double updateVelocity(int i) {
        double v = Math.random() * velocity.get(i)
                + 2 * Math.random() * (pBest.getX().get(i) - x.get(i))
                + 2 * Math.random() * (gBest.getX().get(i) - x.get(i));
        v = v > 0 ? Math.min(v, 5) : Math.max(v, -5);
        velocity.set(i, v);
        return v;
    }
    //位置的更新依赖于速度
    private double updateX(int i) {
        double newX = x.get(i) + velocity.get(i);
        x.set(i, newX);
        return newX;
    }
    //避免粒子跑到合理空间(约束)之外,强制将粒子约束于合理的区间中.
    private void validateX() {
        if (x.get(1) > 400) {
            x.set(1, (double) random.nextInt(401));
        }

        //x2
        double max = 400 - 1.1 * x.get(1);
        if (x.get(2) > max || x.get(2) < 0) {
            x.set(2, random.nextDouble() * max);
        }

        //x3
        max = 484 - 1.21 * x.get(1) - 1.1 * x.get(2);
        if (x.get(3) > max || x.get(3) < 0) {
            x.set(3, random.nextDouble() * max);
        }

        //x4
        max = 532.4 - 1.331 * x.get(1) - 1.21 * x.get(2) - 1.1 * x.get(3);
        if (x.get(4) > max || x.get(4) < 0) {
            x.set(4, random.nextDouble() * max);
        }
    }
}
