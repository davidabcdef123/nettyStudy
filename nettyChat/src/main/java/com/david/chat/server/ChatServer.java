package com.david.chat.server;

import com.david.chat.protocol.IMDecoder;
import com.david.chat.protocol.IMEncoder;
import com.david.chat.server.handler.HttpHandler;
import com.david.chat.server.handler.SocketHandler;
import com.david.chat.server.handler.WebSocktHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

/**
 * @author: sun chao
 * Create at: 2019-04-18
 * <p>
 * ChannelOption.SO_BACKLOG说明
 * client发送SYN到server，将状态修改为SYN_SEND，如果server收到请求，则将状态修改为SYN_RCVD，并把该请求放到syns queue队列中。 2、server回复SYN+ACK给client，如果client收到请求，则将状态修改为ESTABLISHED，并发送ACK给server。 3、server收到ACK，将状态修改为ESTABLISHED，并把该请求从syns queue中放到accept queue。
 * 在linux系统内核中维护了两个队列：syns queue和accept queue
 * syns queue 用于保存半连接状态的请求，其大小通过/proc/sys/net/ipv4/tcp_max_syn_backlog指定，一般默认值是512，不过这个设置有效的前提是系统的syncookies功能被禁用。互联网常见的TCP SYN FLOOD恶意DOS攻击方式就是建立大量的半连接状态的请求，然后丢弃，导致syns queue不能保存其它正常的请求。
 * accept queue 用于保存全连接状态的请求，其大小通过/proc/sys/net/core/somaxconn指定，在使用listen函数时，内核会根据传入的backlog参数与系统参数somaxconn，取二者的较小值。
 * 如果accpet queue队列满了，server将发送一个ECONNREFUSED错误信息Connection refused到client。
 * 在netty实现中，backlog默认通过NetUtil.SOMAXCONN指定。
 * 当然也可以通过option方法自定义backlog的大小。
 * 前面已经提到过，内核会根据somaxconn和backlog的较小值设置accept queue的大小，如果想扩大accept queue的大小，必须要同时调整这两个参数。
 * https://www.jianshu.com/p/e6f2036621f4
 */
public class ChatServer {

    private static Logger LOG = Logger.getLogger(ChatServer.class);

    private int port = 80;

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workerGroup)
                    //放入class类，类似于ioc，由
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            ChannelPipeline pipeline = socketChannel.pipeline();
                            /** 解析自定义协议 */
                            pipeline.addLast(new IMDecoder());
                            pipeline.addLast(new IMEncoder());
                            pipeline.addLast(new SocketHandler());

                            /** 解析Http请求 */
                            pipeline.addLast(new HttpServerCodec());
                            //主要是将同一个http请求或响应的多个消息对象变成一个 fullHttpRequest完整的消息对象
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            //主要用于处理大数据流,比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的 ,加上这个handler我们就不用考虑这个问题了
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpHandler());

                            /** 解析WebSocket请求 */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                            pipeline.addLast(new WebSocktHandler());
                        }
                    });
            //todo sync ???
            ChannelFuture future = serverBootstrap.bind(this.port).sync();
            LOG.info("服务已启动,监听端口" + this.port);
            future.channel().closeFuture().sync();
            System.out.println("server 结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
