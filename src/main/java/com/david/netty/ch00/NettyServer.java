package com.david.netty.ch00;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by sc on 2019-04-03.
 */
public class NettyServer {

    private static final String IP = "127.0.0.1";
    private static final int port = 6666;
    private static final int BIZ_GROUPS_IZE =  Runtime.getRuntime().availableProcessors() * 2;

    private static final int BIZ_THREAD_SIZE = 100;

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZ_GROUPS_IZE);
    private static final EventLoopGroup workGroup=new NioEventLoopGroup(BIZ_THREAD_SIZE);

    public static void start()throws Exception{

        ServerBootstrap serverBootstrap=initServerBootstrap();

        ChannelFuture channelFuture=serverBootstrap.bind(IP,port).sync();
        //todo 为什么要关闭
        channelFuture.channel().closeFuture().sync();
        System.out.println("server start");
    }

    private static ServerBootstrap initServerBootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                //channel是用于boss线程和child是用于child线程
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline=channel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8))
                                .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                //必须最后加处理类
                                .addLast(new TcpServerHandler());
                    }
                });
        return serverBootstrap;
    }

    protected static void shutdown(){
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("启动Server...");
        NettyServer.start();
    }
}
