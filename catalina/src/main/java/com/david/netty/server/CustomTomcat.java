package com.david.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.apache.log4j.Logger;

/**
 * @author: sun chao
 * Create at: 2019-04-21
 */
public class CustomTomcat extends ChannelInboundHandlerAdapter {

    private static Logger LOG = Logger.getLogger(CustomTomcat.class);

    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //服务端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            socketChannel.pipeline().addLast(new HttpResponseEncoder());
                            //服务端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            socketChannel.pipeline().addLast(new HttpRequestDecoder());
                            //最后处理自己的逻辑
                            socketChannel.pipeline().addLast(new CustomTomcatHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128).
                    childOption(ChannelOption.SO_KEEPALIVE, true);
            //绑定服务端口
            ChannelFuture f = null;

            f = serverBootstrap.bind(port).sync();
            LOG.info("HTTP服务已启动，监听端口:" + port);

            //开始接收客户
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CustomTomcat().start(8080);
    }
}
