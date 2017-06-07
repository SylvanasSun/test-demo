//package com.sun.sylvanas.concurrent.akka;
//
//import akka.actor.*;
//import akka.japi.Procedure;
//import com.typesafe.config.ConfigFactory;
//import scala.concurrent.Await;
//import scala.concurrent.Future;
//import scala.concurrent.duration.Duration;
//
//import java.util.concurrent.TimeUnit;
//
//import static akka.pattern.Patterns.ask;
//import static akka.pattern.Patterns.pipe;
//
///**
// * 当我们需要一个有返回值的调用时,可以通过Actor提供的一个契约(Future)追踪到我们的请求.
// * <p>
// * Created by sylvanasp on 2016/12/19.
// */
//public class AkkaFutureDemo {
//    static class ComputeActor extends UntypedActor {
//
//        Procedure<Object> compute = new Procedure<Object>() {
//            @Override
//            public void apply(Object o) throws Exception {
//                if (o instanceof Integer) {
//                    int i = (Integer) o;
//                    getSender().tell(i * i, getSelf());
//                }
//                unhandled(o);
//            }
//        };
//
//        @Override
//        public void onReceive(Object msg) throws Exception {
//            if (msg instanceof Integer)
//                getContext().become(compute);
//            if (msg == AkkaInboxDemo.Msg.DONE)
//                System.out.println("Stop working.");
//            if (msg == AkkaInboxDemo.Msg.CLOSE) {
//                System.out.println("I will shutdown.");
//                getSender().tell(AkkaInboxDemo.Msg.CLOSE, getSelf());
//                getContext().stop(getSelf());
//            }
//            unhandled(msg);
//        }
//    }
//
//    static class PrintActor extends UntypedActor {
//        @Override
//        public void onReceive(Object msg) throws Exception {
//            if (msg instanceof Integer)
//                System.out.println("Printer:" + msg);
//            if (msg == AkkaInboxDemo.Msg.DONE)
//                System.out.println("Stop working.");
//            if (msg == AkkaInboxDemo.Msg.CLOSE) {
//                System.out.println("I will shutdown.");
//                getSender().tell(AkkaInboxDemo.Msg.CLOSE, getSelf());
//                getContext().stop(getSelf());
//            }
//            unhandled(msg);
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        ActorSystem system = ActorSystem.create("actorSystem", ConfigFactory.defaultOverrides());
//        ActorRef computer = system.actorOf(Props.create(ComputeActor.class), "computer");
//        ActorRef printer = system.actorOf(Props.create(PrintActor.class), "printer");
//
//        //等待future返回
//        Future<Object> future = ask(computer, 5, 1500);
//        int result = (int) Await.result(future, Duration.create(6, TimeUnit.SECONDS));
//        System.out.println("return:" + result);
//
//        //直接导向其他Actor,pipe不会等待
//        future = ask(computer,6,1500);
//        pipe(future,system.dispatcher()).to(printer);
//
//        computer.tell(PoisonPill.getInstance(),ActorRef.noSender());
//    }
//}
