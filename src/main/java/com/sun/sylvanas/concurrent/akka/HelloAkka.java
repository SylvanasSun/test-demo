package com.sun.sylvanas.concurrent.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.typesafe.config.ConfigFactory;

/**
 * Akka基本使用案例.
 * 在Akka中使用一种全新的执行单元-Actor,它的粒度比线程更小.
 * <p>
 * Created by sylvanasp on 2016/12/16.
 */
public class HelloAkka {
    static enum Greeter {
        GREETED, DONE;
    }

    /**
     * 对消息GREETED做出反应,并回应发送者一个DONE.
     */
    static class ReceiveActor extends UntypedActor {
        @Override
        public void onReceive(Object o) throws Exception {
            if (o == Greeter.GREETED) {
                System.out.println("Hello,World!");
                getSender().tell(Greeter.DONE, getSelf());
            } else {
                unhandled(o);
            }
        }
    }

    static class SendActor extends UntypedActor {
        ActorRef receive;

        /**
         * Akka的回调方法,在Actor启动前,会被Akka框架调用,完成一些初始化的工作.
         */
        @Override
        public void preStart() throws Exception {
            //创建ReceiveActor的实例,由于创建的时候使用的是SendActor的上下文所以它属于SendActor的子Actor.
            //参数1为类型,参数2为Actor的名字
            receive = getContext().actorOf(Props.create(ReceiveActor.class, "receive"));
            System.out.println("Receive Actor Path:" + receive.path());
            //对ReceiveAtor发送一个GREETED
            receive.tell(Greeter.GREETED, getSelf());
        }

        @Override
        public void onReceive(Object o) throws Exception {
            if (o == Greeter.DONE) {
                receive.tell(Greeter.GREETED, getSelf());
                //将自己停止
                getContext().stop(getSelf());
            } else {
                unhandled(o);
            }
        }
    }

    /**
     * ActorSystem表示管理和维护Actor的系统.
     * 参数1为系统名称.
     * 参数2为配置文件.
     */
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("Hello", ConfigFactory.defaultOverrides());
        ActorRef send = system.actorOf(Props.create(SendActor.class, "send"));
        System.out.println("Send Actor Path:" + send.path());
    }
}
