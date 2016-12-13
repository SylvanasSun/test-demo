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
    public static void main(String[] args) throws IOException, InterruptedException {
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress("localhost", 8000), null,
                new CompletionHandler<Void, Object>() {
                    public void completed(Void result, Object attachment) {
                        client.write(ByteBuffer.wrap("Hello,World!".getBytes()), null,
                                new CompletionHandler<Integer, Object>() {
                                    public void completed(Integer result, Object attachment) {
                                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                                        client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                                            public void completed(Integer result, ByteBuffer attachment) {
                                                attachment.flip();
                                                System.out.println(new String(attachment.array()));
                                                try {
                                                    client.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            public void failed(Throwable exc, ByteBuffer attachment) {

                                            }
                                        });
                                    }

                                    public void failed(Throwable exc, Object attachment) {

                                    }
                                });
                    }

                    public void failed(Throwable exc, Object attachment) {

                    }
                });
        //由于主线程马上结束,这里等待上述处理全部完成
        Thread.sleep(1000);
    }
}
