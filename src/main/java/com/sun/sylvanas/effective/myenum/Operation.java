package com.sun.sylvanas.effective.myenum;

import java.util.HashMap;
import java.util.Map;

/**
 * 四则运算枚举类
 * <p>
 * Created by sylvanasp on 2016/10/4.
 */
public enum Operation {

    PLUS("+") {
        double apply(double x, double y) {
            return x + y;
        }
    },

    MINUS("-") {
        double apply(double x, double y) {
            return x - y;
        }
    },

    TIMES("*") {
        double apply(double x, double y) {
            return x * y;
        }
    },

    DIVIDE("/") {
        double apply(double x, double y) {
            return x / y;
        }
    };

    abstract double apply(double x, double y);

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    /**
     * 根据定制的字符串表示法返回相应的枚举
     */
    private static final Map<String, Operation> stringToEnum = new HashMap<String, Operation>();

    static {
        for (Operation op : values()) {
            stringToEnum.put(op.toString(), op);
        }
    }

    public static Operation fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

}
