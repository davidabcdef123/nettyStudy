package com.david.netty.ch01;

import io.netty.channel.*;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by sc on 2019-04-03.
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        channelRead(channelHandlerContext,s);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 为新连接发送庆祝
        ctx.write("Welcome to " +
                InetAddress.getLocalHost().getHostName() + "!/r/n");
        ctx.write("It is " + new Date() + " now./r/n");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Generate and write a response.
        String response;
        boolean close = false;
        if (msg == null || msg .equals("")) {
            response = "Please type something./r/n";
        } else if ("bye".equals(msg)) {
            response = "Have a good day!/r/n";
            close = true;
        } else {
            response = "Did you say '" + msg + "'?/r/n";
        }
        ChannelFuture future = ctx.write(response);
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
