//package com.sun.sylvanas.concurrent.akka;
//
//import akka.actor.*;
//import com.typesafe.config.ConfigFactory;
//import scala.concurrent.duration.Duration;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * Akka提供了一个Inbox(收件箱)的组件,它可以很方便地对Actor进行消息发送和接收.
// * <p>
// * Created by sylvanasp on 2016/12/17.
// */
//public class AkkaInboxDemo {
//    public static enum Msg {
//        WORKING, DONE, CLOSE;
//    }
//
//    static class Worker extends UntypedActor {
//        @Override
//        public void preStart() throws Exception {
//            System.out.println(getSelf().path() + "is start.");
//        }
//
//        @Override
//        public void onReceive(Object o) throws Exception {
//            if (o == Msg.WORKING) {
//                System.out.println("I am working.");
//            } else if (o == Msg.DONE) {
//                System.out.println("Stop working.");
//            } else if (o == Msg.CLOSE) {
//                System.out.println("I will shutdown.");
//                getSender().tell(Msg.class, getSelf());
//                getContext().stop(getSelf());
//            }
//            unhandled(o);
//        }
//    }
//
//    /**
//     * 使用Inbox与Worker交互
//     */
//    public static void main(String[] args) {
//        ActorSystem system = ActorSystem.create("inboxDemo", ConfigFactory.defaultOverrides());
//        ActorRef worker = system.actorOf(Props.create(Worker.class), "worker");
//
//        Inbox inbox = Inbox.create(system);
//        //监控worker并发送消息.
//        inbox.watch(worker);
//        inbox.send(worker, Msg.WORKING);
//        inbox.send(worker, Msg.DONE);
//        inbox.send(worker, Msg.CLOSE);
//
//        //接收worker的消息.
//        while (true) {
//            Object msg = inbox.receive(Duration.create(1, TimeUnit.SECONDS));
//            if (msg == Msg.CLOSE) {
//                System.out.println("Worker is Closing.");
//            } else if (msg instanceof Terminated) {
//                System.out.println("Worker is dead.");
//                system.shutdown();
//                break;
//            } else {
//                System.out.println(msg);
//            }
//        }
//    }
//}
