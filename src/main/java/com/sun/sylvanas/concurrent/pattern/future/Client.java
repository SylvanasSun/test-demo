package com.sun.sylvanas.concurrent.pattern.future;

/**
 * 主要实现了获取FutureData,并开启构造RealData的线程.
 * 在接收请求后,很快的返回FutureData.
 * 它不会等待数据构造完毕再返回,而是立即返回FutureData.
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public class Client {
    public Data request(final String queryStr) {
        final FutureData futureData = new FutureData();
        //在单独的线程中构造RealData
        new Thread() {
            @Override
            public void run() {
                RealData realData = new RealData(queryStr);
                futureData.setRealData(realData);
            }
        }.start();
        return futureData; //FutureData会被立即返回
    }

    public static void main(String[] args) {
        Client client = new Client();
        Data data = client.request("name");
        System.out.println("请求完毕");
        try {
            //使用sleep模拟对其他业务逻辑的处理,在这个过程中RealData被创建,利用了等待时间
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("数据 = " + data.getResult());
    }
}
