package com.sun.sylvanas.effective.serializable;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * 使用序列化代理的模式完成对Period类的序列化
 * 序列化代理也有两个局限性:
 * 1. 它不能与可以被客户端扩展的类兼容
 * 2. 它不能与对象图中包含循环的某些类兼容
 * <p>
 * Created by sylvanasp on 2016/10/11.
 */
public final class Period implements Serializable {

    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.compareTo(this.end) > 0)
            throw new IllegalArgumentException(start + " after " + end);
    }

    /**
     * 序列化代理类,一般使用一个私有的静态嵌套类,精确地表示外围类的实例的逻辑状态.
     */
    private static class SerializationProxy implements Serializable {
        private final Date start;
        private final Date end;

        SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        /**
         * 返回一个逻辑上相当的外围类的实例,这个方法的出现导致序列化系统在反序列化时将序列化
         * 代理转变回外围类的实例
         */
        private Object readResolve() {
            return new Period(start, end);
        }

        private static final long serialVersionUID = 234098243823485285L;
    }

    /**
     * writeReplace方法在序列化之前,将外围类的实例转变成了它的序列化代理
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * 用于确保writeReplace的约束条件
     */
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }

}
