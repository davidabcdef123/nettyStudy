package com.david.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @author: sun chao
 * Create at: 2019-04-13
 */
public class AIOClient {

    private final AsynchronousSocketChannel client;

    public AIOClient() throws Exception{
        //Asynchronous
        //BIO   Socket
        //NIO   SocketChannel
        //AIO   AsynchronousSocketChannel
        this.client = AsynchronousSocketChannel.open();
    }

    public void connect(String host, int port) throws Exception {
        //Viod什么都不是
        //也是实现一个匿名的接口
        //这里只做写操作
        client.connect(new InetSocketAddress(host, port), null, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                client.write(ByteBuffer.wrap(("这是一条测试数据"+System.currentTimeMillis()).getBytes()));
                 System.out.println("已发送至服务器");
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });

        //下面这一段代码是只读数据
        final ByteBuffer bb = ByteBuffer.allocate(1024);
        client.read(bb, null, new CompletionHandler<Integer, Object>() {
            //实现IO操作完成的方法
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("获取反馈结果" + new String(bb.array()));
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }


    public static void main(String[] args) throws InterruptedException {
        int count=10;
        final CountDownLatch latch = new CountDownLatch(count);

        for(int i=0;i<count;i++){
            latch.countDown();
            new Thread(){

                @Override
                public void run() {
                    try {
                        latch.await();
                        new AIOClient().connect("localhost",8080);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }
        Thread.sleep(1000 * 60 * 10);
    }
}
