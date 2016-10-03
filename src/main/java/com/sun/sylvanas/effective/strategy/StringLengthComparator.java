package com.sun.sylvanas.effective.strategy;

/**
 * 具体的策略类,用于比较字符串长度
 * <p>
 * Created by sylvanasp on 2016/10/3.
 */
public class StringLengthComparator implements Comparator<String> {

    /**
     * 私有化构造方法
     */
    private StringLengthComparator() {
    }

    /**
     * 通过公有的静态final域导出
     */
    public static final StringLengthComparator STRING_LENGTH_COMPARATOR
            = new StringLengthComparator();

    /**
     * 如果第一个字符串的长度小于第二个,则返回一个负整数,
     * 如果两个字符串的长度相等,则返回0,
     * 如果第一个字符串大于第二个字符串的长度,则返回一个正整数.
     */
    public int compare(String t1, String t2) {
        return t1.length() - t2.length();
    }

}
