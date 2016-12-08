package com.sun.sylvanas.concurrent.pattern.future;

/**
 * 实现了一个快速返回的RealData包装,它只是一个RealData的虚拟实现.
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public class FutureData implements Data {
    protected RealData realData = null;
    protected boolean isReady = false;

    public synchronized void setRealData(RealData realData) {
        if (isReady) {
            return;
        }
        this.realData = realData;
        isReady = true;
        notifyAll(); //RealData已被注入,通知getResult()
    }

    /**
     * 当调用getResult()时,如果实际的数据没有准备好,那么程序会阻塞.
     * 等待RealData准备好并注入到FutureData中,才最终返回数据.
     */
    public synchronized String getResult() {
        while (!isReady) {
            try {
                wait(); //一直等待,直到RealData被注入
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return realData.getResult();
    }
}
