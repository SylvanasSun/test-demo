package com.sun.sylvanas.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * 使用UDP协议实现一个简易的服务器
 * <p>
 * Created by sylvanasp on 2017/1/4.
 */
public class UDPServer {
    private static final int PORT = 29999;
    private static final int DATA_LEN = 4096; //数据报大小
    private byte[] inBuffer = new byte[DATA_LEN]; // 用于接收数据的字节数组
    private String[] messages = {
            "Hello,World.", "Hello,Java.", "Hello,UDP.", "Hello,Sylvanas."
    };
    private DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
    private DatagramPacket outPacket = null;

    public void start() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(PORT);

            //循环接收数据
            for (int i = 0; i < 100; i++) {
                datagramSocket.receive(inPacket);
                System.out.println(new String(inBuffer, 0, inPacket.getLength()));
                //发送给客户端信息
                byte[] sendData = messages[i % 4].getBytes();
                outPacket = new DatagramPacket(sendData, sendData.length, inPacket.getSocketAddress());
                datagramSocket.send(outPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
