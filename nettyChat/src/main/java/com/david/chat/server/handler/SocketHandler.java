package com.david.chat.server.handler;

import com.david.chat.processor.MsgProcessor;
import com.david.chat.protocol.IMMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * @author: sun chao
 * Create at: 2019-04-18
 */
public class SocketHandler extends SimpleChannelInboundHandler<IMMessage> {

    private static Logger LOG = Logger.getLogger(SocketHandler.class);

    private MsgProcessor processor = new MsgProcessor();


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IMMessage imMessage) throws Exception {
        processor.sendMsg(channelHandlerContext.channel(), imMessage);
    }

    /**
     * tcp链路建立成功后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("Socket Client: 有客户端连接：" + processor.getAddress(ctx.channel()));
    }

    //不活动的
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("channelInactive-不活动了 ctx=" + ctx.getClass());
        super.channelInactive(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOG.info("服务端Handler创建...class=" + ctx.getClass());
        super.handlerAdded(ctx);
    }

    //所以说每个连接一个handler->ChannelHandlerContext->channel->
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel client = ctx.channel();
        processor.logout(client);
        super.handlerRemoved(ctx);
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.info("Socket Client: 与客户端断开连接:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
