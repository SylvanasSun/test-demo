package com.sun.sylvanas.concurrent.akka.pso;

/**
 * 全局最优解消息类型,使用PsoValue表示一个可行的解.
 * <p>
 * Created by sylvanasp on 2016/12/20.
 */
public final class GbestMsg {
    private final PsoValue value;

    public GbestMsg(PsoValue v) {
        this.value = v;
    }

    public PsoValue getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
