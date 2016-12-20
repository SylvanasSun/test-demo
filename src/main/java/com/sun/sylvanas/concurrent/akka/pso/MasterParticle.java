package com.sun.sylvanas.concurrent.akka.pso;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

/**
 * Master粒子,用于管理和通知全局最优解
 * <p>
 * Created by sylvanasp on 2016/12/20.
 */
public class MasterParticle extends UntypedActor {
    private PsoValue gBest = null;

    /**
     * 当它收到一个个体最优解时,会将其与全局最优解进行比较,
     * 如果产生了新的全局最优解,就更新这个全局最优解并通知所有的粒子.
     */
    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof PbestMsg) {
            PsoValue pBest = ((PbestMsg) msg).getValue();
            if (gBest == null || gBest.getValue() < pBest.getValue()) {
                //更新全局最优解,通知所有粒子
                System.out.println(msg + "\n");
                gBest = pBest;
                ActorSelection selection = getContext().actorSelection("/user/particle_*");
                selection.tell(new GbestMsg(gBest), getSelf());
            }
        } else {
            unhandled(msg);
        }
    }
}
