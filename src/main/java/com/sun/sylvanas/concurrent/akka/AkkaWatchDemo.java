//package com.sun.sylvanas.concurrent.akka;
//
//import akka.actor.*;
//import com.typesafe.config.ConfigFactory;
//
///**
// * 当Actor的工作过程中发生异常,Actor会需要重启,当Actor重启时,会回调preRestart()
// * 接着系统会创建一个新的Actor对象实例.当新的Actor实例创建后,会回调postRestart(),表示启动完成.
// * 同时新的实例将替代旧的实例.
// * <p>
// * Created by sylvanasp on 2016/12/17.
// */
//public class AkkaWatchDemo {
//
//    static class MyWorker extends UntypedActor {
//        public static enum Msg {
//            WORKING, DONE, CLOSE;
//        }
//
//        /**
//         * 在Actor启动前调用.
//         */
//        @Override
//        public void preStart() throws Exception {
//            System.out.println("MyWorker is starting.");
//        }
//
//        /**
//         * 在Actor停止时调用.
//         */
//        @Override
//        public void postStop() throws Exception {
//            System.out.println("MyWorker is stopping.");
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
//                getSender().tell(Msg.CLOSE, getSelf());
//                getContext().stop(getSelf());
//            } else {
//                unhandled(o);
//            }
//        }
//    }
//
//    /**
//     * 使用一个WatchActor(本质上也是一个Actor),来监控一个Actor.
//     * 如果将来这个被监控的Actor退出终止,WatchActor会接收到一条Terminated消息.
//     */
//    static class WatchActor extends UntypedActor {
//        public WatchActor(ActorRef ref) {
//            getContext().watch(ref);
//        }
//
//        @Override
//        public void onReceive(Object o) throws Exception {
//            if (o instanceof Terminated) {
//                System.out.println(String.format("%s has terminated,shutting down system",
//                        ((Terminated) o).getActor().path()));
//                getContext().system().shutdown();
//            } else {
//                unhandled(o);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        ActorSystem actorSystem = ActorSystem.create("ActorSystem", ConfigFactory.defaultOverrides());
//        ActorRef worker = actorSystem.actorOf(Props.create(MyWorker.class), "worker");
//        //Props.create(),它的第一个参数为Actor类型,第二个参数为这个Actor的构造函数的参数.
//        ActorRef watchActor = actorSystem.actorOf(Props.create(WatchActor.class, worker), "watchActor");
//        worker.tell(MyWorker.Msg.WORKING, ActorRef.noSender());
//        worker.tell(MyWorker.Msg.DONE, ActorRef.noSender());
//        //发送毒药丸,它会直接毒死接收方.
//        worker.tell(PoisonPill.getInstance(), ActorRef.noSender());
//    }
//
//}
