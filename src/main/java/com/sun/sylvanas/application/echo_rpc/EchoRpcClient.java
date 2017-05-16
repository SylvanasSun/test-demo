package com.sun.sylvanas.application.echo_rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by SylvanasSun on 2017/5/16.
 */
public class EchoRpcClient {

    public void call() {
        EchoService service = (EchoService) Proxy.newProxyInstance(EchoService.class.getClassLoader(),
                new Class<?>[]{EchoService.class}, new EchoServiceProxyHandler());
        System.out.println(service.echo("hello"));
    }

    private class EchoServiceProxyHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Socket socket = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress("127.0.0.1", 8888));
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());

                oos.writeUTF("com.sun.sylvanas.application.echo_rpc.EchoServiceImpl");
                oos.writeUTF(method.getName());
                oos.writeObject(method.getParameterTypes());
                oos.writeObject(args);

                return ois.readObject();
            } finally {
                if (ois != null)
                    ois.close();
                if (oos != null)
                    oos.close();
                if (socket != null)
                    socket.close();
            }
        }
    }

    public static void main(String[] args) {
        new EchoRpcClient().call();
    }

}
