package cn.hhspace.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/2/17 12:25
 * @Descriptions: 学习Java Nio
 */
public class NioTest {
    public static void main(String[] args) {
        /**
         * FileChannel可以通过FileInputStream/FileOutputStream/RandomAccessFile的实例方法getChannel()来获取，也可以通过静态方法FileChannel.open(Path, OpenOption)来打开
         */
        try {
            RandomAccessFile file = new RandomAccessFile("a.txt", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(5);
            byte[] data = "Hello, Java NIO.".getBytes();
            for (int i = 0; i < data.length; ) {
                buf.put(data, i, Math.min(data.length - i, buf.limit() - buf.position()));
                buf.flip();
                i += channel.write(buf);
                buf.compact();
            }
            channel.force(false);
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
