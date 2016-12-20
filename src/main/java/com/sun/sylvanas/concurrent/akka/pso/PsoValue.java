package com.sun.sylvanas.concurrent.akka.pso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 包含两个信息:
 * 1.表示投资规划的方案(即每一年分别需要投资多少钱).
 * 2.表示投资方案的总收益.
 * Created by sylvanasp on 2016/12/20.
 */
public final class PsoValue {
    private final double value;//表示这组投资方案的收益值.
    //x[1] x[2] x[3] x[4]分别表示第一年,第二年,第三年,第四年的投资额.
    //这里忽略了x[0](它在我们的程序中是没有作用的)
    private final List<Double> x;

    public PsoValue(double v, List<Double> x) {
        this.value = v;
        List<Double> b = new ArrayList<Double>(5);
        b.addAll(x);
        this.x = Collections.unmodifiableList(b);
    }

    public double getValue() {
        return value;
    }

    public List<Double> getX() {
        return x;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("value:").append(value).append("\n")
                .append(x.toString());
        return sb.toString();
    }
}
