package com.sun.sylvanas.concurrent.pattern.future;

/**
 * 它是最终需要使用的数据模型,这里使用sleep()函数模拟构造缓慢的过程.
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public class RealData implements Data {
    protected final String result;

    public RealData(String para) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            sb.append(para);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        result = sb.toString();
    }

    public String getResult() {
        return result;
    }
}
