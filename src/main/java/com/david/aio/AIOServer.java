package com.david.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: sun chao
 * Create at: 2019-04-13
 */
public class AIOServer {

    private final int port;

    public static final String key = "count";

    public static void main(String[] args) throws Exception {
        int port=8080;
        new AIOServer(port);
    }

    public AIOServer(int port) throws Exception {
        this.port = port;
        listen();
    }

    private void listen() throws Exception{
        //线程缓冲池，为了体现异步
        ExecutorService executorService = Executors.newCachedThreadPool();
        //给线程池初始化一个线程
        AsynchronousChannelGroup threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService,1);

        final AsynchronousServerSocketChannel server=AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        System.out.println("服务已启动，监听端口" + port);

        final Map<String, Integer> count = new ConcurrentHashMap<>();
        count.put(key, 0);
        //开始等待客户端连接
        //实现一个CompletionHandler 的接口，匿名的实现类
        server.accept(null,new CompletionHandler<AsynchronousSocketChannel,Object>() {

            final ByteBuffer buffer = ByteBuffer.allocate(1024);

            //实现io操作完成的方法
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                count.put(key, count.get(key) + 1);

                System.out.println("complete count="+count.get(key));
                //只要拿数据，捡现成的,我们都是懒人，IO操作都不用关心了
                //System.out.println("IO操作成功，开始获取数据");
                try {
                    buffer.clear();
                    result.read(buffer).get();
                    buffer.flip();
                    result.write(buffer.put("aaaaa".getBytes()));
                    buffer.flip();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        result.close();
                        server.accept(null, this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("IO操作是失败: " + exc);
            }

        });
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }


    }
}
