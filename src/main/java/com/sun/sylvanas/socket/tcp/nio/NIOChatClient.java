package com.sun.sylvanas.socket.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * 使用NIO实现ChatClient
 * <p>
 * Created by sylvanasp on 2017/1/10.
 */
public class NIOChatClient {
    private static final int PORT = 8001;
    private static final String HOST = "127.0.0.1";
    private static Charset charset = Charset.forName("utf-8");
    private Selector selector = null;
    private SocketChannel client = null;

    {
        try {
            selector = Selector.open();
            client = SocketChannel.open();
            client.configureBlocking(false);
            client.connect(new InetSocketAddress(HOST, PORT));
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(this.getClass().getName() + " initialize fail.");
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        if (client.isConnectionPending()) {
            client.finishConnect();
        }
        //开起一条线程接收服务端的信息
        new Thread(new ReceiveServerThread()).start();
        //监控键盘输入的信息并发送给服务端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            client.write(charset.encode(scanner.nextLine()));
        }
    }

    private class ReceiveServerThread implements Runnable {
        @Override
        public void run() {
            try {
                while (selector.select() > 0) {
                    for (SelectionKey sk : selector.selectedKeys()) {
                        selector.selectedKeys().remove(sk);
                        if (sk.isReadable()) {
                            SocketChannel channel = (SocketChannel) sk.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            try {
                                while (channel.read(buffer) > 0) {
                                    buffer.flip();
                                    System.out.println("Chat: " + charset.decode(buffer));
                                    buffer.clear();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                sk.cancel();
                                sk.channel().close();
                            }
                            sk.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOChatClient().start();
    }
}
