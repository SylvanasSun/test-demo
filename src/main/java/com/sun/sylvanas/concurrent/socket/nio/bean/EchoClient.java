package com.sun.sylvanas.concurrent.socket.nio.bean;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * 封装了一个队列,保存在需要回复给这个客户端的所有信息
 * <p>
 * Created by sylvanasp on 2016/12/12.
 */
public class EchoClient {
    private LinkedList<ByteBuffer> outq;

    public EchoClient() {
        outq = new LinkedList<ByteBuffer>();
    }

    public LinkedList<ByteBuffer> getOutputQueue() {
        return outq;
    }

    public void enqueue(ByteBuffer bb) {
        outq.addFirst(bb);
    }
}
