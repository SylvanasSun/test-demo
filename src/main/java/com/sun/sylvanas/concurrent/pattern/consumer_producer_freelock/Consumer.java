package com.sun.sylvanas.concurrent.pattern.consumer_producer_freelock;

import com.lmax.disruptor.WorkHandler;

/**
 * 消费者线程,继承WorkHandler<E>(来自于Disruptor框架)
 * <p>
 * Created by sylvanasp on 2016/12/8.
 */
public class Consumer implements WorkHandler<Data> {
    /**
     * 数据的读取已经由Disruptor进行封装,onEvent()为框架的回调方法,我们只需要处理数据即可.
     */
    public void onEvent(Data data) throws Exception {
        System.out.println(Thread.currentThread().getId() + ":Event: --" +
                data.getValue() * data.getValue() + "--");
    }
}
