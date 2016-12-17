package com.sun.sylvanas.concurrent.akka;

import akka.actor.*;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Akka使用一个路由器组件Router来封装消息的调度.
 * <p>
 * Created by sylvanasp on 2016/12/17.
 */
public class AkkaRouterDemo {
    static class WatchActor extends UntypedActor {
        private Router router; //路由器组件Router

        {
            List<Routee> routees = new ArrayList<Routee>();
            for (int i = 0; i < 5; i++) {
                ActorRef worker = getContext().actorOf(Props.create(AkkaInboxDemo.Worker.class), "worker_" + i);
                getContext().watch(worker);
                routees.add(new ActorRefRoutee(worker));
            }
            //需要指定路由策略和一组被路由的Actor(Routee)
            //这里使用了轮询消息发送策略.
            //其他策略为:
            //BroadcastRoutingLogic 广播策略
            //RandomRoutingLogic 随机投递策略
            //SmallestMailboxRoutingLogic 空闲Actor优先投递策略
            router = new Router(new RoundRobinRoutingLogic(), routees);
        }

        @Override
        public void onReceive(Object o) throws Exception {
            if (o instanceof AkkaInboxDemo.Msg) {
                //当有消息需要传递给这5个Worker时,只需要将消息投递给这个Router即可.
                router.route(o, getSender());
            } else if (o instanceof Terminated) {
                //当一个Worker停止工作时,将其从工作组中移除.
                router = router.removeRoutee(((Terminated) o).actor());
                System.out.println(((Terminated) o).actor().path() + " is closed," +
                        "routees=" + router.routees().size());
                //如果工作组中已经没有可用的Actor,则直接关闭系统.
                if (router.routees().size() == 0) {
                    System.out.println("Close system");
                    RouteMain.flag.send(false);
                    getContext().system().shutdown();
                }
            } else {
                unhandled(o);
            }
        }
    }

    static class RouteMain {
        public static Agent<Boolean> flag = Agent.create(true, ExecutionContexts.global());

        public static void main(String[] args) throws InterruptedException {
            ActorSystem system = ActorSystem.create("routeDemo", ConfigFactory.defaultOverrides());
            ActorRef watcher = system.actorOf(Props.create(WatchActor.class), "watcher");
            int i = 1;
            while (flag.get()) {
                watcher.tell(AkkaInboxDemo.Msg.WORKING, ActorRef.noSender());
                if (i % 10 == 0) {
                    watcher.tell(AkkaInboxDemo.Msg.CLOSE, ActorRef.noSender());
                }
                i++;
                Thread.sleep(100);
            }
        }
    }
}
