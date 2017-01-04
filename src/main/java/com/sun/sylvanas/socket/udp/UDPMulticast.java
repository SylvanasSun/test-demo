package com.sun.sylvanas.socket.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

/**
 * 使用MulticastScoket实现多点广播
 * <p>
 * Created by sylvanasp on 2017/1/4.
 */
public class UDPMulticast {
    private static final int PORT = 30001;
    private static final String IP = "233.0.0.1";
    private static final int DATA_LEN = 4096; //数据报大小
    private byte[] inBuffer = new byte[DATA_LEN];
    private MulticastSocket socket = null;
    private DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
    private DatagramPacket outPacket = null;

    public void start() {
        try {
            socket = new MulticastSocket(PORT);
            //加入指定IP的多点广播组
            socket.joinGroup(InetAddress.getByName(IP));
            //设置是否接收回报信息(自己发送的数据信息)
            socket.setLoopbackMode(false);
            //初始化outPacket
            outPacket = new DatagramPacket(new byte[0], 0, InetAddress.getByName(IP), PORT);
            //开启一个线程监听收到的信息
            new Thread(new ReceiveThread()).start();
            //监听键盘输入
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                //将键盘输入的数据发送出去
                outPacket.setData(scanner.nextLine().getBytes());
                //发送数据报
                socket.send(outPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收信息并处理的线程
     */
    private class ReceiveThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    socket.receive(inPacket);
                    System.out.println("receive: " +
                            new String(inBuffer, 0, inPacket.getLength()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                //发现异常,将本socket退出多点广播组,并关闭虚拟机
                try {
                    socket.leaveGroup(InetAddress.getByName(IP));
                    socket.close();
                    System.exit(1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
