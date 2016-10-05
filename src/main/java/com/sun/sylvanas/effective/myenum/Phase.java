package com.sun.sylvanas.effective.myenum;

import java.util.EnumMap;
import java.util.Map;

/**
 * 用EnumMap代替序数索引
 * <p>
 * Created by sylvanasp on 2016/10/5.
 */
public enum Phase {

    SOLID, LIQUID, GAS;

    public enum Transition {

        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase src;
        private final Phase dst;

        Transition(Phase src, Phase dst) {
            this.src = src;
            this.dst = dst;
        }

        /**
         * 这个map的键是一个枚举(起始阶段),值为另一个map,
         * 第二个map的键为第二个枚举(目标阶段),它的值为结果(阶段过渡),
         * 即: Map(起始阶段,Map(目标阶段,阶段过渡))
         */
        private static final Map<Phase, Map<Phase, Transition>> m =
                new EnumMap<Phase, Map<Phase, Transition>>(Phase.class);

        static {
            // 初始化外部map
            for (Phase p : Phase.values()) {
                m.put(p, new EnumMap<Phase, Transition>(Phase.class));
            }
            // 利用每个状态过度常量提供的起始信息和目标信息初始化内部map
            for (Transition trans : Transition.values()) {
                m.get(trans.src).put(trans.dst, trans);
            }
        }

        public static Transition from(Phase src, Phase dst) {
            return m.get(src).get(dst);
        }
    }

}
