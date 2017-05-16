package com.sun.sylvanas.application.echo_rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by SylvanasSun on 2017/5/16.
 */
public class EchoRpcServer {

    private static final int CORE_NUMBER = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(CORE_NUMBER);
    private static ServerSocket server;
    private static Socket client;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public void start() {
        try {
            server = new ServerSocket(8888);
            while (true) {
                try {
                    client = server.accept();
                    System.out.println("[SERVER] Accept connection " + client.getRemoteSocketAddress());
                    threadPool.execute(new ExecuteThread());
                } catch (IOException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ExecuteThread implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setName("Echo-RPC-Server-Thread.");
            try {
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());
                // 读取客户端发送的服务信息
                String serviceName = in.readUTF();
                String methodName = in.readUTF();
                Class<?>[] paramterTypes = (Class<?>[]) in.readObject();
                Object[] args = (Object[]) in.readObject();
                // 使用反射调用这个服务并将结果写回客户端
                Class<?> service = Class.forName(serviceName);
                Method method = service.getMethod(methodName, paramterTypes);
                out.writeObject(method.invoke(service.newInstance(), args));
            } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null)
                        out.close();
                    if (in != null)
                        in.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new EchoRpcServer().start();
    }

}
