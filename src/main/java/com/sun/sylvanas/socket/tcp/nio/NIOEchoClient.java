package com.sun.sylvanas.socket.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;

/**
 * 使用NIO实现 EchoClient
 * <p>
 * Created by sylvanasp on 2017/1/8.
 */
@SuppressWarnings("Duplicates")
public class NIOEchoClient {
    private Selector selector = null;
    private SocketChannel client = null;
    private static Charset charset = Charset.forName("UTF-8");
    private static final int PORT = 23333;
    private static final String HOST = "127.0.0.1";

    {
        try {
            selector = SelectorProvider.provider().openSelector();
            client = SocketChannel.open();
            client.configureBlocking(false);
            client.connect(new InetSocketAddress(HOST, PORT));
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        if (client.isConnectionPending()) {
            client.finishConnect();
        }
        //向服务端输出内容
        client.write(ByteBuffer.wrap("Mitsuha!".getBytes()));
        //开启监听读事件线程
        new Thread(new ReadThread()).start();
    }

    private class ReadThread implements Runnable {
        @Override
        public void run() {
            try {
                while (selector.select() > 0) {
                    for (SelectionKey sk : selector.selectedKeys()) {
                        selector.selectedKeys().remove(sk);
                        if (sk.isReadable()) {
                            SocketChannel channel = (SocketChannel) sk.channel();
                            //读取服务端发来的信息
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            String content = "";
                            while (channel.read(buffer) > 0) {
                                buffer.flip();
                                content += charset.decode(buffer);
                                buffer.clear();
                            }
                            //继续等待后续消息
                            sk.interestOps(SelectionKey.OP_READ);
                            if (content.length() > 0) {
                                System.out.println("Chat: " + content);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOEchoClient().start();
    }
}
