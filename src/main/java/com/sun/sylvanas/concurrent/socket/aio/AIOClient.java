package com.sun.sylvanas.concurrent.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * AIO实现Client,全部采用异步回调实现.
 * <p>
 * Created by sylvanasp on 2016/12/13.
 */
public class AIOClient {
    private static AsynchronousSocketChannel client;
    private final static int PORT = 8000;
    private final static String HOST = "localhost";

    public AIOClient() {
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            System.out.println("Failed on initialization");
            e.printStackTrace();
        }
    }

    public void connect() {
        System.out.println("Client connect from " + HOST + "/" + PORT);
        client.connect(new InetSocketAddress(HOST, PORT), null, new CompletionHandler<Void, Object>() {
            public void completed(Void result, Object attachment) {
                System.out.println("Client connect success!");
                client.write(ByteBuffer.wrap("Hello,World!".getBytes()), null,
                        new CompletionHandler<Integer, Object>() {
                            ByteBuffer buffer = ByteBuffer.allocate(1024);

                            public void completed(Integer result, Object attachment) {
                                System.out.println("Client write success!");
                                buffer.clear();
                                client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                                    public void completed(Integer result, ByteBuffer attachment) {
                                        System.out.println("Client read success!");
                                        buffer.flip();
                                        System.out.println("Form Server content:" + attachment.toString());
                                        try {
                                            client.close();
                                            System.out.println("Client close!");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    public void failed(Throwable exc, ByteBuffer attachment) {
                                        System.out.println("Client read failed!");
                                    }
                                });
                            }

                            public void failed(Throwable exc, Object attachment) {
                                System.out.println("Client write failed!");
                            }
                        });
            }

            public void failed(Throwable exc, Object attachment) {
                System.out.println("Client connect failed!");
            }
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new AIOClient().connect();
        //由于主线程马上结束,这里等待上述处理全部完成
        Thread.sleep(1000);
    }
}
