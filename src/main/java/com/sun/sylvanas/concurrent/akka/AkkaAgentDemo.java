package com.sun.sylvanas.concurrent.akka;

import akka.actor.*;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Mapper;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 当多个Actor需要对同一个共享变量进行读写时,Agent组件就实现了这个功能.
 * 一个Agent提供了对一个变量的异步更新,当多个Actor同时改变Agent时,将会在ExecutionContext中被并发调度执行.
 * Agent的修改可以使用两个方法send()或者alter().它们都可以向Agent发送一个修改动作.
 * send()方法没有返回值,alter()方法会返回一个Future对象便于跟踪Agent的执行.
 * <p>
 * Created by sylvanasp on 2016/12/19.
 */
public class AkkaAgentDemo {
    public static class CounterActor extends UntypedActor {
        //定义累加动作actionAddMapper,它的作用是对Agent的值进行修改处理.
        Mapper addMapper = new Mapper<Integer, Integer>() {
            @Override
            public Integer apply(Integer parameter) {
                return parameter + 1;
            }
        };

        @Override
        public void onReceive(Object msg) throws Exception {
            if (msg instanceof Integer) {
                for (int i = 0; i < 10000; i++) {
                    //希望能够知道Future何时结束
                    scala.concurrent.Future<Integer> future = counterAgent.alter(addMapper);
                    futures.add((Future<Integer>) future);
                }
                getContext().stop(getSelf());
            }
            unhandled(msg);
        }
    }

    public static Agent<Integer> counterAgent = Agent.create(0, ExecutionContexts.global());
    static ConcurrentLinkedQueue<Future<Integer>> futures = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("actorSystem", ConfigFactory.defaultOverrides());
        ActorRef[] counters = new ActorRef[10];
        //创建10个CounterActor对象.
        for (int i = 0; i < counters.length; i++) {
            counters[i] = system.actorOf(Props.create(CounterActor.class), "counter_" + i);
        }
        //使用Inbox发送消息给CounterActor,进行累加操作.
        Inbox inbox = Inbox.create(system);
        for (int i = 0; i < counters.length; i++) {
            inbox.send(counters[i], i);
            inbox.watch(counters[i]);
        }

        int closeCount = 0;
        //等待所有Actor全部结束
        while (true) {
            Object msg = inbox.receive(Duration.create(1, TimeUnit.SECONDS));
            if (msg instanceof Terminated) {
                closeCount++;
                if (closeCount == counters.length) {
                    break;
                }
            } else {
                System.out.println(msg);
            }
        }
        //等待所有的累加线程完成,因为它们都是异步的.
//        Futures.sequence(futures, system.dispatcher()).onComplete(
//                new OnComplete<Iterable<Integer>>() {
//                    @Override
//                    public void onComplete(Throwable throwable, Iterable<Integer> integers) throws Throwable {
//                        System.out.println("counterAgent=" + counterAgent.get());
//                        system.shutdown();
//                    }
//                }, system.dispatcher()
//        );
        System.out.println("counterAgent=" + counterAgent.get());
    }
}
