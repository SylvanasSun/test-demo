package com.sun.sylvanas.socket.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用NIO实现ChatServer
 * <p>
 * Created by sylvanasp on 2017/1/10.
 */
public class NIOChatServer {
    private static final int PORT = 8001;
    private static Charset charset = Charset.forName("utf-8");
    private static Map<Socket, Long> SPEND_STATUS = new HashMap<>();
    private Selector selector = null;
    private ServerSocketChannel server = null;

    {
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(PORT));
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(this.getClass().getName() + " initialize fail.");
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        while (selector.select() > 0) {
            //遍历SelectorKey
            for (SelectionKey sk : selector.selectedKeys()) {
                selector.selectedKeys().remove(sk);
                if (sk.isAcceptable()) {
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    //注册client
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Accept Client Address: " + client.socket().getInetAddress());
                    //继续等待其他客户端连接
                    sk.interestOps(SelectionKey.OP_ACCEPT);
                }
                if (sk.isReadable()) {
                    if (!SPEND_STATUS.containsKey(((SocketChannel) sk.channel()).socket())) {
                        long s = System.currentTimeMillis();
                        //记录开始时间
                        SPEND_STATUS.put(((SocketChannel) sk.channel()).socket(), s);
                    }
                    SocketChannel channel = (SocketChannel) sk.channel();
                    String content = "";
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    try {
                        while (channel.read(buffer) > 0) {
                            buffer.flip();
                            content += charset.decode(buffer);
                            System.out.println("Receive Client Message: " + content);
                            buffer.clear();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        //发生异常,删除这个key并释放资源
                        sk.cancel();
                        sk.channel().close();
                    }
                    //继续等待后续消息
                    sk.interestOps(SelectionKey.OP_READ);
                    //将收到的信息Echo给客户端
                    if (content.length() > 0) {
                        channel.write(charset.encode(content));
                        long end = System.currentTimeMillis();
                        Long start = SPEND_STATUS.get(((SocketChannel) sk.channel()).socket());
                        System.out.println("SPEND: " + (end - start) + "ms");
                        SPEND_STATUS.remove(((SocketChannel) sk.channel()).socket());
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOChatServer().start();
    }
}
