package com.sun.sylvanas.netty.ws;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Created by SylvanasSun on 11/23/2017.
 */
public class SecureChatServer extends ChatServer {

    private final SSLContext context;

    public SecureChatServer(SSLContext context) {
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup) {
        return new SecureChatServerInitializer(channelGroup, context);
    }

    public static void main(String[] args) throws CertificateException, NoSuchAlgorithmException {
        if (args.length != 1) {
            System.err.println("Please give port as argument.");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        SSLContext context = SSLContext.getDefault();
        SecureChatServer endpoint = new SecureChatServer(context);
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }

}
