package com.david.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sc on 2019-04-02.
 */
@Slf4j
public class BIOServer {

    private static int DEAFULT_PORT=7777;

    //单例的serverSocker
    private static ServerSocket serverSocket;

    //根据传入参数设置监听端口，如果没有参数调用以下方法并使用默认值
    public static void start()throws IOException{
        //使用默认值
        start(DEAFULT_PORT);
    }

    private synchronized static void start(int port) throws IOException {
        if(serverSocket !=null){
            return;
        }
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务端已启动，端口号:" + port);

            while (true){
                Socket socket=serverSocket.accept();
                new Thread(new ServerHandler(socket)).start();
            }
        } finally {
            //一些必要的清理工作
            if (serverSocket != null) {
                System.out.println("服务端已关闭。");
                serverSocket.close();
                serverSocket = null;
            }
        }
    }
}
