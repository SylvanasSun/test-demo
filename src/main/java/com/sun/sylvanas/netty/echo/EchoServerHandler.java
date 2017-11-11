package com.sun.sylvanas.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Created by SylvanasSun on 11/11/2017.
 */
// Flag this ChannelHandler will be can other ChannelHandler shared.
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.printf("Server received: %s \n", in.toString(CharsetUtil.UTF_8));
        ctx.write(in); // send to sender but not flush outbound message.
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // flush pending message to remote node and close channel.
        // pending message is message that current temporary store in the ChannelOutboundBuffer.
        // it will be try write out to the socket in the next invoke flush() or writeAndFlush().
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
