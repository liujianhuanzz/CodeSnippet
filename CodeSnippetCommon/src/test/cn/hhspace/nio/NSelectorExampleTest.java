package cn.hhspace.nio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NSelectorExampleTest {

    @Test
    public void test() throws IOException {
        // 启动服务端
        new Thread(() -> {
            try {
                NSelectorExample.main(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 等待服务端启动
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 连接服务端并发送数据
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8080));
        ByteBuffer buffer = ByteBuffer.wrap("Hello World".getBytes());
        socketChannel.write(buffer);

        // 读取返回的数据
        ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
        int bytesRead = socketChannel.read(responseBuffer);
        responseBuffer.flip();
        byte[] responseBytes = new byte[bytesRead];
        responseBuffer.get(responseBytes);
        String response = new String(responseBytes);

        // 打印返回的数据内容
        System.out.println("Response: " + response);

        // 断言返回的数据内容是否正确
        assertEquals("Hello World", response);

        // 关闭连接
        socketChannel.close();
    }
}


