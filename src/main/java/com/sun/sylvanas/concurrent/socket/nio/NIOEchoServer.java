package com.sun.sylvanas.concurrent.socket.nio;

import com.sun.sylvanas.concurrent.socket.nio.bean.EchoClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用NIO构造的EchoServer.
 * NIO中的关键组件:
 * Channel:它类似于流,一个Channel可以和文件或者网络Socket对应.
 * 如果Channel对应着一个Socket,那么往这个Channel中些数据,就等同于向Socket中写入数据.
 * Buffer:可以简单地理解成一个内存区域或者byte数组,数据需要包装成Buffer的形式才能和Channel交互.
 * Selector:在Channel的众多实现中,有一个SelectableChannel实现,表示可被选择的通道.
 * 任何一个SelectableChannel都可以将自己注册到一个Selector中,这样,这个Channel就能被
 * Selector所管理,而一个Selector可以管理多个SelectableChannel.
 * 当SelectableChannel的数据准备好时,Selector就会接到通知,得到那些已经准备好的数据.
 * 而SocketChannel就是SelectableChannel的一种.
 * <p>
 * 一个Selector可以由一个线程进行管理,而一个SelectableChannel则可以表示一个客户端连接.
 * 因此可以构成由一个或者极少数线程,来处理大量客户端连接的结构.
 * <p>
 * Created by sylvanasp on 2016/12/12.
 */
public class NIOEchoServer {
    private Selector selector;//用于处理所有的网络连接
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    //用于统计在某一个Socket上花费的时间,key为Socket,value为时间戳
    public static Map<Socket, Long> time_status = new HashMap<Socket, Long>(10240);

    /**
     * 核心方法,用于启动NIO Server
     */
    private void startServer() throws IOException {
        //通过工厂方法获得一个Selector对象的实例
        selector = SelectorProvider.provider().openSelector();
        //获得表示服务端的SocketChannel实例
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //将这个SocketChannel设置为非阻塞模式
        ssc.configureBlocking(false);

        //端口绑定,将这个SocketChannel绑定在8000端口
        InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), 8000);
        ssc.socket().bind(isa);
        // 将这个ServerSocketChannel绑定到Selector上,并注册它感兴趣的操作为Accept
        // 当Selector发现ServerSocketChannel有新的客户端连接时,就会通知ServerSocketChannel进行处理
        // 返回值SelectionKey表示一对Selector和Channel的关系.
        // 当Selector或者Channel被关闭时,对应的SelectionKey失效.
        SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

        //一个无穷循环,它的主要任务是等待-分发网络消息
        for (; ; ) {
            //这是一个阻塞方法,如果当前没有任何数据准备好,它就会等待,直到有数据可读
            //它的返回值是已经准备就绪的SelectionKey的数量
            selector.select();
            //获取准备好的SelectionKey
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            long e = 0;
            //使用迭代器遍历这个SelectionKey集合
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                //将这个元素移除,如果不做这一步的话,就会重复处理相同的SelectionKey.
                iterator.remove();

                //判断当前SelectionKey是否在Acceptable状态,如果是,则进行客户端的接收(doAccept())
                if (sk.isAcceptable()) {
                    doAccept(sk);
                }
                //判断Channel是否已经可以读了,如果是就进行读取(doRead())
                //这里为了统计系统处理每一个连接的时间,在读取数据之前记录了一个时间戳
                else if (sk.isValid() && sk.isReadable()) {
                    if (!time_status.containsKey(((SocketChannel) sk.channel()).socket())) {
                        time_status.put
                                (((SocketChannel) sk.channel()).socket(), System.currentTimeMillis());
                        doRead(sk);
                    }
                }
                //判断Channel是否已经可以写了,如果是就进行写入(doWrite())
                //同时在写入完成后,根据读取前的时间戳,输出处理这个Socket连接的耗时
                else if (sk.isValid() && sk.isWritable()) {
                    doWrite(sk);
                    e = System.currentTimeMillis();
                    long b = time_status.remove(((SocketChannel) sk.channel()).socket());
                    System.out.println("spend:" + (e - b) + "ms");
                }
            }
        }
    }

    /**
     * 与客户端建立连接
     * 当有一个新的客户端连接接入时,就会有一个新的Channel产生代表这个连接.
     */
    private void doAccept(SelectionKey sk) {
        ServerSocketChannel server = (ServerSocketChannel) sk.channel();
        SocketChannel clientChannel;
        try {
            clientChannel = server.accept();
            clientChannel.configureBlocking(false);
            //将新生成的Channel注册到Selector选择器上,并只对读操作感兴趣
            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
            EchoClient echoClient = new EchoClient();
            //将EchoClient附加到表示这个连接的SelectionKey上
            clientKey.attach(echoClient);

            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            System.out.println("Accepted connection from " + clientAddress.getHostAddress() + ".");
        } catch (Exception e) {
            System.out.println("Failed to accept new client.");
            e.printStackTrace();
        }
    }

    private void doRead(SelectionKey sk) {
        SocketChannel channel = (SocketChannel) sk.channel();
        ByteBuffer bb = ByteBuffer.allocate(8192);
        int len;

        try {
            len = channel.read(bb);
            if (len < 0) {
                disconnect(sk);
                return;
            }
        } catch (Exception e) {
            System.out.println("Failed to read from client.");
            e.printStackTrace();
            disconnect(sk);
            return;
        }
        //重置缓冲区
        bb.flip();
        threadPool.execute(new HandleMsg(sk, bb));
    }

    private void doWrite(SelectionKey sk) {
        SocketChannel channel = (SocketChannel) sk.channel();
        EchoClient echoClient = (EchoClient) sk.attachment();
        LinkedList<ByteBuffer> outputQueue = echoClient.getOutputQueue();

        ByteBuffer bb = outputQueue.getLast();
        try {
            int len = channel.write(bb);
            if (len == -1) {
                disconnect(sk);
                return;
            }
            if (bb.remaining() == 0) {
                outputQueue.removeLast();
            }
        } catch (IOException e) {
            System.out.println("Failed to write to client.");
            e.printStackTrace();
            disconnect(sk);
        }
        //当队列的长度为0时,需要将写事件(OP_WRITE)从兴趣操作中移除
        if (outputQueue.size() == 0) {
            sk.interestOps(SelectionKey.OP_READ);
        }
    }

    class HandleMsg implements Runnable {
        SelectionKey sk;
        ByteBuffer bb;

        public HandleMsg(SelectionKey sk, ByteBuffer bb) {
            this.sk = sk;
            this.bb = bb;
        }

        public void run() {
            EchoClient echoClient = (EchoClient) sk.attachment();
            echoClient.enqueue(bb);
            sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            //强迫selector立即返回
            selector.wakeup();
        }
    }

    private void disconnect(SelectionKey sk) {
        sk.cancel();
    }
}
