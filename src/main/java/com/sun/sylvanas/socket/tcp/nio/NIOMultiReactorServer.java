package com.sun.sylvanas.socket.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 多Reactor的NIO Server,主Reactor负责接受客户端请求,由子Reactor负责读写操作.
 * <p>
 * Created by SylvanasSun on 2017/4/29.
 */
public class NIOMultiReactorServer {

    private final static int CORE_PROCESSOR_NUM = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress("127.0.0.1", 8001));
        server.register(selector, SelectionKey.OP_ACCEPT);

        Processor[] processors = new Processor[CORE_PROCESSOR_NUM];
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new Processor();
        }

        int index = 0;
        while (selector.select() > 0) {
            for (SelectionKey sk : selector.selectedKeys()) {
                selector.selectedKeys().remove(sk);
                if (sk.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) sk.channel();
                    SocketChannel client = channel.accept();
                    System.out.println("[SERVER] Accept request from " + client.getRemoteAddress());
                    Processor processor = processors[((index++) % CORE_PROCESSOR_NUM)];
                    processor.register(client);
                    processor.wakeup();
                }
            }
        }
    }

    private static class Processor {
        private final Executor executor = Executors.newFixedThreadPool(CORE_PROCESSOR_NUM);
        private Selector selector;
        private SocketChannel channel;

        public Processor() {
            try {
                selector = Selector.open();
                start();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(this.getClass().getName() + " initialize failed.");
            }
        }

        public void register(SocketChannel channel) throws IOException {
            this.channel = channel;
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        }

        public void wakeup() {
            selector.wakeup();
        }

        public void start() throws IOException {
            executor.execute(() -> {
                try {
                    while (true) {
                        if (selector.select(500) <= 0) {
                            continue;
                        }
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey sk = iterator.next();
                            iterator.remove();
                            if (sk.isReadable()) {
                                SocketChannel channel = (SocketChannel) sk.channel();
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                int read = channel.read(byteBuffer);
                                if (read < 0) {
                                    channel.close();
                                    sk.cancel();
                                    System.out.println("[SERVER] Read ended.");
                                    continue;
                                } else if (read == 0) {
                                    System.out.println("[SERVER] Read message size is 0.");
                                    continue;
                                } else {
                                    System.out.println("[SERVER] Read message: " + new String(byteBuffer.array()));
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

}
