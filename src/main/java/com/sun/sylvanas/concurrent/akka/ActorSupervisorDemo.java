//package com.sun.sylvanas.concurrent.akka;
//
//import akka.actor.*;
//import akka.japi.Function;
//import com.typesafe.config.ConfigFactory;
//import scala.Option;
//import scala.concurrent.duration.Duration;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * 在Akka框架内,父Actor可以对子Actor进行监督,监控Actor的行为是否有异常.
// * 监督策略可以分为2种:
// * 1.OneForOneStrategy:父Actor只会对出问题的子Actor进行处理,比如重启或者停止.
// * 2.AllForOneStrategy:父Actor会对出问题的子Actor以及它所有的兄弟都进行处理.
// * OneForOneStrategy是Akka的默认策略.
// * <p>
// * Created by sylvanasp on 2016/12/17.
// */
//public class ActorSupervisorDemo {
//
//    /**
//     * 在一个指定的策略中,我们可以对Actor的失败情况进行相应的处理.
//     * 比如:当失败时,我们可以无视这个错误,继续执行Actor.
//     * 或者:可以重启这个Actor,甚至可以让这个Actor彻底停止工作.
//     * 要指定这些监督行为,只要构造一个自定义的监督策略即可.
//     */
//    public static class Supervisor extends UntypedActor {
//        /**
//         * 定义了一个OneForOneStrategy的监督策略.
//         * 在这个监督策略中,运行Actor在遇到错误后,在1分钟内进行3次重试.
//         * 如果超过这个频率,那么就会直接杀死Actor.
//         */
//        private static SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES),
//                new Function<Throwable, SupervisorStrategy.Directive>() {
//                    @Override
//                    public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
//                        if (throwable instanceof ArithmeticException) {
//                            System.out.println("meet ArithmeticException,just resume.");
//                            //继续指定这个Actor,不做任何处理.
//                            return SupervisorStrategy.resume();
//                        } else if (throwable instanceof NullPointerException) {
//                            System.out.println("meet NullPointerException,just restart.");
//                            //进行Actor的重启.
//                            return SupervisorStrategy.restart();
//                        } else if (throwable instanceof IllegalArgumentException) {
//                            System.out.println("meet IllegalArgumentException,just stop.");
//                            //直接停止Actor
//                            return SupervisorStrategy.stop();
//                        } else {
//                            //其他异常向上抛出,由更顶层的Actor处理.
//                            return SupervisorStrategy.escalate();
//                        }
//                    }
//                }
//        );
//
//        /**
//         * 覆盖父类的supervisorStrategy()方法,设置使用自定义的监督策略.
//         */
//        @Override
//        public SupervisorStrategy supervisorStrategy() {
//            return strategy;
//        }
//
//        @Override
//        public void onReceive(Object o) throws Exception {
//            if (o instanceof Props) {
//                //当接收一个Props对象时,会根据这个Props配置生成一个restartActor.由当前Actor监督.
//                getContext().actorOf((Props) o, "restartActor");
//            } else {
//                unhandled(o);
//            }
//        }
//    }
//
//    public static class RestartActor extends UntypedActor {
//        public enum Msg {
//            DONE, RESTART;
//        }
//
//        @Override
//        public void preStart() throws Exception {
//            System.out.println("preStart hashcode:" + this.hashCode());
//        }
//
//        @Override
//        public void postStop() throws Exception {
//            System.out.println("postStop hashcode:" + this.hashCode());
//        }
//
//        @Override
//        public void preRestart(Throwable reason, Option<Object> message) throws Exception {
//            super.preRestart(reason, message);
//            System.out.println("preRestart hashcode:" + this.hashCode());
//        }
//
//        @Override
//        public void postRestart(Throwable reason) throws Exception {
//            super.postRestart(reason);
//            System.out.println("postRestart hashcode:" + this.hashCode());
//        }
//
//        @Override
//        public void onReceive(Object o) throws Exception {
//            if (o == Msg.DONE) {
//                getContext().stop(getSelf());
//            } else if (o == Msg.RESTART) {
//                //模拟抛出NullPoniterException
//                System.out.println(((Object) null).toString());
//                //模拟抛出ArithmeticException
//                double a = 0 / 0;
//            }
//            unhandled(o);
//        }
//    }
//
//    public static void customStrategy(ActorSystem system) {
//        ActorRef supervisor = system.actorOf(Props.create(Supervisor.class), "Supervisor");
//        //对Supervisor发送一个RestartActor的Props(这个消息会使Supervisor创建RestartActor)
//        supervisor.tell(Props.create(RestartActor.class), ActorRef.noSender());
//        //选中RestartActor实例,向它发送100条RESTART消息,会使RestartActor抛出内部模拟的异常.
//        ActorSelection selection = system.actorSelection("akka://lifecycle/user/Supervisor/restartActor");
//        for (int i = 0; i < 100; i++) {
//            selection.tell(RestartActor.Msg.RESTART, ActorRef.noSender());
//        }
//    }
//
//    public static void main(String[] args) {
//        ActorSystem system = ActorSystem.create("lifecycle", ConfigFactory.defaultOverrides());
//        customStrategy(system);
//    }
//}
