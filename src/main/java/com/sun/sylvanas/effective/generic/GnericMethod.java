package com.sun.sylvanas.effective.generic;

import java.util.Iterator;
import java.util.List;

/**
 * Created by sylvanasp on 2016/10/4.
 */
public class GnericMethod {

    // Generic singleton factory pattern
    /**
     * 泛型单例模式,这是一个恒等函数.
     */
    private static UnaryFunction<Object> IDENTITY_FUNCTION =
            new UnaryFunction<Object>() {
                public Object apply(Object arg) {
                    return arg;
                }
            };


    @SuppressWarnings("unchecked")
    public static <T> UnaryFunction<T> identityFunction() {
        return (UnaryFunction<T>) IDENTITY_FUNCTION;
    }

    /**
     * 根据元素的自然顺序计算列表的最大值并返回
     * 因为所有的Comparable和Comparator都是消费者,所以根据PECS原则使用super
     */
    public static <T extends Comparable<? super T>> T max(List<? extends T> list) {
        Iterator<? extends T> iterator = list.iterator();
        T result = iterator.next();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (t.compareTo(result) > 0) {
                result = t;
            }
        }
        return result;
    }

}
