package com.david.nio.channel;

import com.david.nio.buffer.Buffers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sc on 2019-04-02.
 */
public class FileChannelDemo {

    public static void main(String[] args) {
        /*创建文件，向文件中写入数据*/
        /*如果文件不存在，创建该文件,文件后缀是不是文本文件不重要*/
        try {
            File file = new File("e:/noi_utf8.data");
            if (!file.exists()) {
                file.createNewFile();
            }
            /*根据文件输出流创建与这个文件相关的通道*/
            FileOutputStream fos = new FileOutputStream(file);
            //获取管道
            FileChannel fc = fos.getChannel();
            /*创建ByteBuffer对象， position = 0, limit = 64*/
            ByteBuffer byteBuffer = ByteBuffer.allocate(64);
            /*向ByteBuffer中放入字符串UTF-8的字节, position = 17, limit = 64*/
            byteBuffer.put("hello world 123".getBytes("UTF-8"));
            /*flip方法  position = 0, limit = 17*/
            byteBuffer.flip();
            /*write方法使得ByteBuffer的position到 limit中的元素写入通道中*/
            fc.write(byteBuffer);
            /*clear方法使得position = 0， limit = 64*/
            byteBuffer.clear();

            /*下面的代码同理*/
            byteBuffer.put("你好，世界 456".getBytes("UTF-8"));
            byteBuffer.flip();

            fc.write(byteBuffer);
            byteBuffer.clear();

            fos.close();
            fc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*从刚才的文件中读取字符序列*/
        try {
            Path path = Paths.get("e:/noi_utf8.data");
            FileChannel fileChannel = FileChannel.open(path);

            ByteBuffer byteBuffer=ByteBuffer.allocate((int) (fileChannel.size()+1));

            Charset utf8 = Charset.forName("UTF-8");
            /*阻塞模式，读取完成才能返回*/
            fileChannel.read(byteBuffer);

            byteBuffer.flip();
            CharBuffer charBuffer = utf8.decode(byteBuffer);
            System.out.print(charBuffer.toString());
            byteBuffer.clear();
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
