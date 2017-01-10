package com.sun.sylvanas.socket.tcp.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用Blocking IO 实现 EchoServer
 * <p>
 * Created by sylvanasp on 2017/1/7.
 */
public class BIOEchoServer {
    private static boolean flag = true; //服务器循环flag
    private static final int PORT = 8000; //端口号
    private static ExecutorService threadPool = Executors.newCachedThreadPool();//线程池
    ServerSocket server = null;

    public void start() throws IOException {
        server = new ServerSocket(PORT);
        while (flag) {
            Socket client = server.accept(); //等待客户端连接
            long start = System.currentTimeMillis();
            //开启一条线程进行对客户端的处理操作
            threadPool.execute(new ServerHandler(client));
            long end = System.currentTimeMillis();
            System.out.println("Server Spend: " + (end - start) + " ms.");
        }
    }

    public void stop() {
        flag = false;
        try {
            if (server != null) server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }

    private class ServerHandler implements Runnable {
        private Socket client = null;

        private ServerHandler(Socket client) {
            this.client = client;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public void run() {
            Thread.currentThread().setName("ServerHandler - " + new Random().nextInt(101));
            BufferedReader reader = null;
            PrintWriter writer = null;
            try {
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream(), true);
                //读取从客户端发送的信息
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("From Client Message: " + line + " " + client.getInetAddress().toString());
                    //发送信息给客户端
                    writer.println("Mitsuha!" + " " + server.getInetAddress().toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) writer.close();
                    if (reader != null) reader.close();
                    if (client != null) client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        BIOEchoServer server = new BIOEchoServer();
        server.start();
    }
}
