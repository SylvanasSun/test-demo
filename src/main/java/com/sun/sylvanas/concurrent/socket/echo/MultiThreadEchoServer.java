package com.sun.sylvanas.concurrent.socket.echo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程的EchoServer,服务器会为每一个客户端连接启用一个线程.
 * 为了接收客户端连接,服务器还会额外使用一个派发线程.
 * <p>
 * Created by sylvanasp on 2016/12/12.
 */
public class MultiThreadEchoServer {
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    static class HandleMsg implements Runnable {
        Socket clientSocket;

        public HandleMsg(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            BufferedReader is = null;
            PrintWriter os = null;
            try {
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                os = new PrintWriter(clientSocket.getOutputStream(), true);
                //从InputStream当中读取客户端所发送的数据
                String inputLine = null;
                long b = System.currentTimeMillis();
                while ((inputLine = is.readLine()) != null) {
                    os.println(inputLine);
                }
                long e = System.currentTimeMillis();
                System.out.println("spend:" + (e - b) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                    if (os != null) os.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在8000端口上进行等待,一旦有新的客户端连接,它就会根据这个连接创建HandleMsg线程进行处理.
     */
    public static void main(String[] args) {
        ServerSocket echoServer = null;
        Socket clientSocket = null;
        try {
            echoServer = new ServerSocket(8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                clientSocket = echoServer.accept();
                System.out.println(clientSocket.getRemoteSocketAddress() + "connect!");
                threadPool.execute(new HandleMsg(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
