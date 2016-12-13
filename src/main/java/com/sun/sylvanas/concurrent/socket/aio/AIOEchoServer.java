package com.sun.sylvanas.concurrent.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * AIO是在IO操作已经完成后,再给线程发出通知.
 * 因此,AIO是完全不会阻塞的,此时,业务逻辑将变成一个回掉函数.
 * 等待IO操作完成后,由系统自动触发.
 * <p>
 * Created by sylvanasp on 2016/12/13.
 */
public class AIOEchoServer {
    private final static int PORT = 8000;
    private AsynchronousServerSocketChannel server;

    public AIOEchoServer() throws IOException {
        server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
    }

    public void start() {
        System.out.println("Server start PORT:" + PORT);
        //注册事件和事件完成后的处理器
        //AsynchronousServerSocketChannel.accept()会立即返回,它并不会真的去等待客户端
        //第一个参数为附件,可以是任意类型,作用是让当前线程和后续的回调方法可以共享信息
        //第二个参数是CompletionHandler接口
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            /**
             * 在accept()成功时调用
             */
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                System.out.println(Thread.currentThread().getName());
                Future<Integer> writeResult = null;
                try {
                    buffer.clear();
                    result.read(buffer).get(100, TimeUnit.SECONDS);
                    buffer.flip();
                    writeResult = result.write(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        /**
                         * 服务器进行下一个客户端连接的准备.
                         * 同时关闭当前正在处理的客户端连接.
                         * 但在关闭之前,为了确保之前的write()操作已经完成.
                         * 因此,使用Future.get()方法进行等待.
                         */
                        server.accept(null, this);
                        writeResult.get();
                        result.close();
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }

            /**
             * 在accept()失败时调用
             */
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed " + exc);
            }
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new AIOEchoServer().start();
        /**
         * 由于AIO都是异步的,并不会像阻塞方法那样进行等待.
         * 因此,如果想让程序驻守执行,需要模拟等待语句.
         * 否则,start()方法会立即返回,不会真的等待客户端到来.
         * 导致程序运行完成,主线程就将退出.
         */
        while (true) {
            Thread.sleep(1000);
        }
    }
}
