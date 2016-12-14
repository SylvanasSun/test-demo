package com.sun.sylvanas.concurrent.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * 使用NIO实现的客户端
 * <p>
 * Created by sylvanasp on 2016/12/12.
 */
public class NIOEchoClient {
    private static Selector selector;
    private static SocketChannel client;
    private final static int PORT = 8000;
    private final static String HOST = "localhost";

    private void init() {
        System.out.println("SocketChannel and Selector Initialization.");
        try {
            selector = SelectorProvider.provider().openSelector();
            client = SocketChannel.open();
            client.configureBlocking(false);//设为非阻塞
            client.connect(new InetSocketAddress(HOST, PORT));
            client.register(selector, SelectionKey.OP_CONNECT);//注册到选择器
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to initialization.");
        }
    }

    /**
     * 主方法
     */
    public void working(String content) {
        init();
        while (true) {
            //当选择器关闭时,退出无穷循环,结束程序.
            if (!selector.isOpen())
                break;
            try {
                selector.select();
                //遍历SelectionKey
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isValid() && key.isConnectable()) {
                        doConnect(key,content);
                    } else if (key.isValid() && key.isReadable()) {
                        doRead(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doRead(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            channel.read(buffer);
            buffer.flip();
            System.out.println("Form Server Echo:" + new String(buffer.array()).trim());
            //释放客户端
            channel.close();
            key.selector().close();
            System.out.println("Channel and Selector has been release.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to read.");
        }
    }

    private void doConnect(SelectionKey key,String content) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            //如果正在连接中,则完成连接.
            if (channel.isConnectionPending()) {
                channel.finishConnect();
            }
            channel.configureBlocking(false);
            channel.write(ByteBuffer.wrap(content.getBytes()));
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.out.println("Failed to connect.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NIOEchoClient().working("Hello,World!");
    }
}
