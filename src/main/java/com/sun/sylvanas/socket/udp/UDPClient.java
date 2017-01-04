package com.sun.sylvanas.socket.udp;


import java.io.IOException;
import java.net.*;
import java.util.Scanner;

/**
 * 使用UDP协议实现一个简易的客户端
 * <p>
 * Created by sylvanasp on 2017/1/4.
 */
public class UDPClient {
    private static final int DESTINATION_PORT = 29999;
    private static final String DESTINATION_IP = "127.0.0.1";
    private static final int DATA_LEN = 4096;
    private byte[] inBuffer = new byte[DATA_LEN];
    private DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
    private DatagramPacket outPacket = null;

    public void start() {
        try {
            //创建一个客户端,使用随机端口
            DatagramSocket socket = new DatagramSocket();
            //用一个空字节数组初始化outPacket
            outPacket = new DatagramPacket(new byte[0], 0,
                    InetAddress.getByName(DESTINATION_IP), DESTINATION_PORT);
            //监听键盘输入
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                byte[] buffer = scanner.nextLine().getBytes();
                //设置字节数据
                outPacket.setData(buffer);
                //发送数据报
                socket.send(outPacket);
                //读取服务端发来的数据
                socket.receive(inPacket);
                System.out.println(new String(inBuffer, 0, inPacket.getLength()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
