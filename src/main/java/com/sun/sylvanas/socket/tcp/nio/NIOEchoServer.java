package com.sun.sylvanas.socket.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用NIO实现Echo Server
 * <p>
 * Created by sylvanasp on 2017/1/8.
 */
public class NIOEchoServer {
    private Selector selector = null;
    private ServerSocketChannel server = null;
    private static Map<Socket, Long> SPEND_STATUS = new HashMap<>(); //用于记录操作耗时
    private static Charset charset = Charset.forName("UTF-8");
    private static final int PORT = 23333;
    private static final String HOST = "127.0.0.1";

    {
        try {
            selector = SelectorProvider.provider().openSelector();
            server = ServerSocketChannel.open();
            //设置server为非阻塞模式
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(HOST, PORT));
            //注册到Selector中
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        while (selector.select() > 0) {
            //遍历SelectionKey
            for (SelectionKey sk : selector.selectedKeys()) {
                selector.selectedKeys().remove(sk);
                if (sk.isAcceptable()) {
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    //将Accept的客户端注册到Selector
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Accepted connection From " + client.socket().getInetAddress());
                    //继续等待其他客户端申请连接
                    sk.interestOps(SelectionKey.OP_ACCEPT);
                }
                if (sk.isReadable()) {
                    if (!SPEND_STATUS.containsKey(((SocketChannel) sk.channel()).socket())) {
                        //记录开始时间
                        SPEND_STATUS.put(((SocketChannel) sk.channel()).socket(), System.currentTimeMillis());
                    }
                    SocketChannel channel = (SocketChannel) sk.channel();
                    //读取客户端发送的信息
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    String content = "";
                    try {
                        while (channel.read(buffer) > 0) {
                            buffer.flip();
                            content += charset.decode(buffer);
                        }
                        System.out.println("From Client Message: " + content + " - " + channel.socket().getInetAddress());
                        //继续读取后续信息
                        sk.interestOps(SelectionKey.OP_READ);
                    } catch (IOException e) {
                        e.printStackTrace();
                        //出现异常,取消此channel的关联信息
                        sk.cancel();
                        if (sk.channel() != null)
                            sk.channel().close();
                    }
                    if (content.length() > 0) {
                        //发送给全部的其他客户端
                        for (SelectionKey key : selector.selectedKeys()) {
                            SelectableChannel tarChannel = key.channel();
                            if (tarChannel instanceof SocketChannel) {
                                SocketChannel sc = (SocketChannel) tarChannel;
                                sc.write(charset.encode(content));
                            }
                        }
                        Long start = SPEND_STATUS.get(((SocketChannel) sk.channel()).socket());
                        System.out.println("SPEND: " + (System.currentTimeMillis() - start) + " ms.");
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        NIOEchoServer server = new NIOEchoServer();
        server.start();
    }
}
