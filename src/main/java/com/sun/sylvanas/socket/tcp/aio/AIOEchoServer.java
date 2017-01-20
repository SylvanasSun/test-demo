package com.sun.sylvanas.socket.tcp.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sylvanasp on 2017/1/14.
 */
public class AIOEchoServer {
    private static final int PORT = 30000;
    private static Charset charset = Charset.forName("utf-8");
    private static List<AsynchronousSocketChannel> channelList = new ArrayList<>();
    private AsynchronousServerSocketChannel server = null;

    {
        //初始化线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        try {
            //以指定线程池创建一个分组管理器
            AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(threadPool);
            //以指定分组打开一个AsynchronousServerSocketChannel
            server = AsynchronousServerSocketChannel.open(channelGroup)
                    .bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(this.getClass().getName() + " initialize fail.");
            throw new RuntimeException(e);
        }
    }

    public void start() {
        server.accept(null, new AccepetHandler());
    }

    private class AccepetHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        @Override
        public void completed(final AsynchronousSocketChannel sc, Object attachment) {
            //将接收到的客户端连接放入List中
            AIOEchoServer.channelList.add(sc);
            //准备接收客户端的下一次连接
            server.accept(null, this);
            //读取客户端发送的消息
            sc.read(buffer, null, new CompletionHandler<Integer, Object>() {

                @Override
                public void completed(Integer result, Object attachment) {
                    buffer.flip();
                    //将buffer中的内容转换为字符串
                    String content = charset.decode(buffer).toString();
                    System.out.println("Receive Client Message: " + content);
                    //将接收到的信息广播给所有客户端
                    for (AsynchronousSocketChannel c : AIOEchoServer.channelList) {
                        try {
                            c.write(ByteBuffer.wrap(content.getBytes())).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    buffer.clear();
                    //准备读取下一次的信息
                    sc.read(buffer, null, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("读取数据失败: " + exc);
                    //将该AsynchronousSocketChannel从List中移除
                    AIOEchoServer.channelList.remove(sc);
                }
            });
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("Accept fail: " + exc);
        }
    }

    public static void main(String[] args) {
        new AIOEchoServer().start();
    }
}
