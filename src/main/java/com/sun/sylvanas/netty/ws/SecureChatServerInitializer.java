package com.sun.sylvanas.netty.ws;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Created by SylvanasSun on 11/23/2017.
 */
public class SecureChatServerInitializer extends ChatServerInitializer {

    private final SSLContext context;

    public SecureChatServerInitializer(ChannelGroup channelGroup, SSLContext context) {
        super(channelGroup);
        this.context = context;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        super.initChannel(channel);
        SSLEngine sslEngine = context.createSSLEngine();
        sslEngine.setUseClientMode(false);
        channel.pipeline().addFirst(new SslHandler(sslEngine));
    }

}
