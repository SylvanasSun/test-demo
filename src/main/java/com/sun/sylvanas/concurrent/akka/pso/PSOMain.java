package com.sun.sylvanas.concurrent.akka.pso;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

/**
 * 程序入口,案例:
 * 假设现在有400万资金,要求4年内使用完,若在第一年使用x万元,则可以得到效益√x万元(效益不能再使用),
 * 当年不用的资金可存入银行,年利率为10%.尝试制定出资金的使用规划,使4年效益之和最大.
 * Created by sylvanasp on 2016/12/20.
 */
public class PSOMain {
    private static final int PARTICLE_COUNT = 100000; //粒子个数

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("psoSystem", ConfigFactory.defaultOverrides());
        system.actorOf(Props.create(MasterParticle.class), "masterParticle");
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            system.actorOf(Props.create(Particle.class), "particle_" + i);
        }
    }
}
