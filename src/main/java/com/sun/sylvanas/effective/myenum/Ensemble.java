package com.sun.sylvanas.effective.myenum;

/**
 * 合奏枚举,演示用实例域代替序数
 * <p>
 * Created by sylvanasp on 2016/10/5.
 */
public enum Ensemble {

    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);

    private final int numberOfMusicans;

    Ensemble(int size) {
        this.numberOfMusicans = size;
    }

    /**
     * 使用实例域代替ordinal序数方法
     */
    public int numberOfMusicans() {
        return numberOfMusicans;
    }

}
