package com.sun.sylvanas.concurrent.socket.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * EchoClient实现
 * <p>
 * Created by sylvanasp on 2016/12/12.
 */
public class MultiThreadEchoClient {
    public static void main(String[] args) throws IOException {
        Socket client = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            client = new Socket();
            client.connect(new InetSocketAddress("localhost", 8000));
            writer = new PrintWriter(client.getOutputStream(), true);
            writer.println("Hello,World!");
            writer.flush();

            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("from server:" + reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (client != null) client.close();
        }
    }
}
