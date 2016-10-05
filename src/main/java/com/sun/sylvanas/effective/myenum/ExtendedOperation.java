package com.sun.sylvanas.effective.myenum;

import java.util.Arrays;
import java.util.Collection;

/**
 * 使用接口扩展枚举类
 * <p>
 * Created by sylvanasp on 2016/10/5.
 */
public enum ExtendedOperation implements IOperation {

    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        public double apply(double x, double y) {
            return x % y;
        }
    };

    private final String symbol;

    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        test(Arrays.asList(ExtendedOperation.values()), x, y);
    }

    private static void test(Collection<? extends IOperation> opSet, double x, double y) {
        for (IOperation op : opSet) {
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
        }
    }

}
