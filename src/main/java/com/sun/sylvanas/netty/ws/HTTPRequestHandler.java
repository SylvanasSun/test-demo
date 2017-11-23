package com.sun.sylvanas.netty.ws;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by SylvanasSun on 11/23/2017.
 */
public class HTTPRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HTTPRequestHandler.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }

    public HTTPRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (wsUri.equalsIgnoreCase(fullHttpRequest.getUri())) {
            // Add count of the reference and transmit into the next handler
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
        } else {
            httpHandle(channelHandlerContext, fullHttpRequest);
        }
    }

    private void httpHandle(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws IOException {
        if (HttpHeaders.is100ContinueExpected(fullHttpRequest)) {
            send100Continue(channelHandlerContext);
        }
        RandomAccessFile file = new RandomAccessFile(INDEX, "r");
        DefaultHttpResponse response = new DefaultHttpResponse(
                fullHttpRequest.getProtocolVersion(), HttpResponseStatus.OK
        );
        response.headers().set(
                HttpHeaders.Names.CONTENT_TYPE,
                "text/html; charset=utf-8"
        );
        boolean keepAlive = HttpHeaders.isKeepAlive(fullHttpRequest);
        if (keepAlive) {
            response.headers().set(
                    HttpHeaders.Names.CONTENT_LANGUAGE,
                    file.length()
            );
            response.headers().set(
                    HttpHeaders.Names.CONNECTION,
                    HttpHeaders.Values.KEEP_ALIVE
            );
        }
        channelHandlerContext.write(response);
        if (channelHandlerContext.pipeline().get(SslHandler.class) == null) {
            channelHandlerContext.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        } else {
            channelHandlerContext.write(new ChunkedNioFile(file.getChannel()));
        }
        ChannelFuture future = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive)
            future.addListener(ChannelFutureListener.CLOSE);
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE
        );
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
