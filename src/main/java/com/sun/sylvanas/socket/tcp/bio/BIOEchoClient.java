package com.sun.sylvanas.socket.tcp.bio;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

/**
 * 使用Blocking IO 实现 EchoClient
 * <p>
 * Created by sylvanasp on 2017/1/7.
 */
public class BIOEchoClient {
    private static final int PORT = 8000;
    private static final String HOST = "localhost";
    private static Socket client = null;

    private static class ClientHandler implements Runnable {
        private Socket client = null;

        private ClientHandler(Socket client) {
            this.client = client;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public void run() {
            Thread.currentThread().setName("ClientHandler - " + new Random().nextInt(101));
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                //向服务端输出信息
                writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                writer.write("What's your name?");
                writer.newLine();
                writer.flush();

                //读取服务端返回的消息
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String lien;
                while ((lien = reader.readLine()) != null) {
                    System.out.println("From Server Message: " + lien);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (writer != null) writer.close();
                    if (client != null) client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        client = new Socket();
        client.connect(new InetSocketAddress(InetAddress.getByName(HOST), PORT));
        new Thread(new ClientHandler(client)).start();
    }
}
