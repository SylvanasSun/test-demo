package com.sun.sylvanas.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * Created by SylvanasSun on 11/24/2017.
 */
public class LogEventMonitor {

    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    public LogEventMonitor(InetSocketAddress address) {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                             @Override
                             protected void initChannel(Channel channel) throws Exception {
                                 ChannelPipeline pipeline = channel.pipeline();
                                 pipeline.addLast(new LogEventDecoder());
                                 pipeline.addLast(new LogEventHandler());
                             }
                         }
                )
                .localAddress(address);
    }

    public Channel bind() {
        return bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop() {
        eventLoopGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: LogEventMonitor <port>");
        }

        LogEventMonitor logEventMonitor = new LogEventMonitor(new InetSocketAddress(Integer.parseInt(args[0])));
        try {
            Channel channel = logEventMonitor.bind();
            System.out.println("LogEventMonitor running");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logEventMonitor.stop();
        }
    }

}
