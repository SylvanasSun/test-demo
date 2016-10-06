package com.sun.sylvanas.effective.method;

import java.util.Date;

/**
 * 用于表示一段不可变的时间周期,演示保护性拷贝.
 * 在这个案例中,也可以使用Date.getTime()返回的long基本类型作为内部的时间表示法,
 * 而不是使用Date对象引用,这样做的原因是,因为Date对象是可变的.
 * <p>
 * Created by sylvanasp on 2016/10/6.
 */
public final class Period {

    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        /**
         * 在构造器中进行保护性拷贝,保护性拷贝是在参数有效性检查之前进行的,
         * 并且有效性检查是针对拷贝之后的对象.
         */
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + " after " + end);
        }
    }

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }

}
