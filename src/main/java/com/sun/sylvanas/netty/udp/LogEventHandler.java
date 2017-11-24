package com.sun.sylvanas.netty.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.tools.ant.taskdefs.BUnzip2;

/**
 * Created by SylvanasSun on 11/24/2017.
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LogEvent logEvent) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(logEvent.getReceivedTimestamp());
        builder.append(" [");
        builder.append(logEvent.getSource().toString());
        builder.append("] [");
        builder.append(logEvent.getLogFile());
        builder.append("] : ");
        builder.append(logEvent.getMessage());
        System.out.println(builder.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
