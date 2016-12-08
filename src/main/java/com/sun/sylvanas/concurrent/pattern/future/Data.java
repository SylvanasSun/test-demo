package com.sun.sylvanas.concurrent.pattern.future;

/**
 * 返回数据的接口,它有两个重要的实现:
 * RealData:真实数据,最终需要获得的信息
 * FutureData:用于提取RealData的一个"订单",它是可以立即返回的
 *
 * Created by sylvanasp on 2016/12/8.
 */
public interface Data {
    public String getResult();
}
