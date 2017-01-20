package com.sun.sylvanas.socket.tcp.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sylvanasp on 2017/1/14.
 */
public class AIOEchoClient {
    private static final int PORT = 30000;
    private static final String HOST = "127.0.0.1";
    private static Charset charset = Charset.forName("utf-8");
    private AsynchronousSocketChannel client = null;

    {
        //初始化线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        try {
            //以指定线程池创建一个分组管理器
            AsynchronousChannelGroup channelGroup =
                    AsynchronousChannelGroup.withThreadPool(threadPool);
            //以指定分组打开一个AsynchronousSocketChannel
            client = AsynchronousSocketChannel.open(channelGroup);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(this.getClass().getName() + " initialize fail.");
            throw new RuntimeException(e);
        }
    }

    public void start() throws ExecutionException, InterruptedException {
        //连接到指定地址
        client.connect(new InetSocketAddress(HOST, PORT)).get();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //向服务端读取信息
        client.read(buffer, null, new CompletionHandler<Integer, Object>() {

            @Override
            public void completed(Integer result, Object attachment) {
                buffer.flip();
                String content = charset.decode(buffer).toString();
                System.out.println("Receive Server Message: " + content);
                buffer.clear();
                //等待服务端的后续信息
                client.read(buffer, null, this);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("读取数据失败: " + exc);
            }
        });
        //向服务端发送消息
        client.write(ByteBuffer.wrap("What's your name?".getBytes())).get();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new AIOEchoClient().start();
    }
}
