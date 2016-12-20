package com.sun.sylvanas.concurrent.akka.pso;

/**
 * 个体最优解消息类型,使用PsoValue表示一个可行的解
 * <p>
 * Created by sylvanasp on 2016/12/20.
 */
public final class PbestMsg {
    private final PsoValue value;

    public PbestMsg(PsoValue v) {
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
