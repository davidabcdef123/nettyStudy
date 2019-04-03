package com.david.netty.ch01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.awt.*;

/**
 * Created by sc on 2019-04-03.
 */
public class Server {

    public static void main(String[] args) throws Exception {
        //Configure the server
        //创建两个EventLoopGroup对象
        //创建boss线程组 ⽤用于服务端接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 创建 worker 线程组 ⽤用于进⾏行行 SocketChannel 的数据读写
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {

            // 创建 ServerBootstrap 对象
            ServerBootstrap bootstrap=new ServerBootstrap();
            //设置使⽤用的EventLoopGroup
            bootstrap.group(bossGroup, workGroup);
            //设置要被实例例化的为 NioServerSocketChannel 类
            bootstrap.channel(NioServerSocketChannel.class);
            //      // 设置 NioServerSocketChannel 的处理理器器
//                    .handler(new LoggingHandler(LogLevel.INFO))
            // 设置连⼊入服务端的 Client 的 SocketChannel 的处理理器器
            bootstrap.childHandler(new ServerInitializer());
            // 绑定端⼝口，并同步等待成功，即启动服务端
            ChannelFuture channelFuture = bootstrap.bind(8888);
            // 监听服务端关闭，并阻塞等待
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅关闭两个 EventLoopGroup 对象
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
