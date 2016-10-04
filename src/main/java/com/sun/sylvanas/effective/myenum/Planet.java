package com.sun.sylvanas.effective.myenum;

/**
 * 太阳系8颗行星枚举类
 * <p>
 * Created by sylvanasp on 2016/10/4.
 */
public enum Planet {

    MECURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6),
    EARTH(5.975e+24, 6.378e6),
    MARS(6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN(5.685e+26, 6.027e7),
    URANUS(8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    private final double mass; //质量
    private final double radius; //半径
    private final double surfaceGravity; //表面重力

    private static final double G = 6.67300E-11;

    /**
     * 构造方法
     * 通过质量和半径计算出表面重力
     */
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass() {
        return mass;
    }

    public double radius() {
        return radius;
    }

    public double surfaceGravity() {
        return surfaceGravity;
    }

    /**
     * 计算表面重量
     */
    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;
    }

}
