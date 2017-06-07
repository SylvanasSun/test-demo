//package com.sun.sylvanas.concurrent.akka;
//
//import akka.actor.*;
//import akka.japi.Procedure;
//import com.typesafe.config.ConfigFactory;
//
///**
// * 一个Actor内部消息处理函数可以根据有多高不同的状态,
// * 在特定的状态下,可以对同一消息进行不同的处理,状态之间也可以任意转换.
// * <p>
// * Created by sylvanasp on 2016/12/17.
// */
//public class AkkaStatusDemo {
//    static class BabyActor extends UntypedActor {
//        public static enum BabyMsg {
//            SLEEP, PLAY, CLOSE;
//        }
//
//        @Override
//        public void preStart() throws Exception {
//            System.out.println(getSelf().path() + "is start.");
//        }
//
//        Procedure<Object> angry = new Procedure<Object>() {
//            @Override
//            public void apply(Object message) throws Exception {
//                System.out.println("angryApply:" + message);
//                if (message == BabyMsg.SLEEP) {
//                    getSender().tell("I am already angry", getSelf());
//                    System.out.println("I am already angry.");
//                } else if (message == BabyMsg.PLAY) {
//                    System.out.println("I like playing.");
//                    //使用become()切换Actor的状态为happy.
//                    getContext().become(happy);
//                }
//            }
//        };
//
//        Procedure<Object> happy = new Procedure<Object>() {
//            @Override
//            public void apply(Object message) throws Exception {
//                System.out.println("happyApply:" + message);
//                if (message == BabyMsg.PLAY) {
//                    getSender().tell("I am already happy :-", getSelf());
//                    System.out.println("I am already happy :-");
//                } else if (message == BabyMsg.SLEEP) {
//                    System.out.println("I don't want to sleep.");
//                    //使用become()切换Actor的状态为angry.
//                    getContext().become(angry);
//                }
//            }
//        };
//
//        /**
//         * become()接收一个Procedure参数,它可以表示为一种Actor的状态.
//         * 同时,更重要的是它封装了在这种状态下的消息处理逻辑.
//         */
//        @Override
//        public void onReceive(Object o) throws Exception {
//            System.out.println("onReceive:" + o);
//            if (o == BabyMsg.SLEEP) {
//                getContext().become(angry);
//            } else if (o == BabyMsg.PLAY) {
//                getContext().become(happy);
//            } else {
//                unhandled(o);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        ActorSystem system = ActorSystem.create("become", ConfigFactory.defaultOverrides());
//        ActorRef babyActor = system.actorOf(Props.create(BabyActor.class), "babyActor");
//        babyActor.tell(BabyActor.BabyMsg.PLAY, ActorRef.noSender());
//        babyActor.tell(BabyActor.BabyMsg.SLEEP, ActorRef.noSender());
//        babyActor.tell(BabyActor.BabyMsg.PLAY, ActorRef.noSender());
//        babyActor.tell(BabyActor.BabyMsg.PLAY, ActorRef.noSender());
//
//        babyActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
//    }
//}
