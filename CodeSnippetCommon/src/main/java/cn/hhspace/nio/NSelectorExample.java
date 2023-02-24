package cn.hhspace.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NSelectorExample {

    public static void main(String[] args) throws IOException {
        // 创建一个Selector对象
        Selector selector = Selector.open();

        // 创建一个ServerSocketChannel并将其注册到Selector中，监听连接事件
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 8080));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, serverSocket.validOps());

        while (true) {
            // 等待事件的发生
            selector.select(1000);

            // 获取事件集合
            Set<java.nio.channels.SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<java.nio.channels.SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                java.nio.channels.SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    // 如果是连接事件，接受连接，并将新的SocketChannel注册到Selector中，监听读取事件
                    SocketChannel client = serverSocket.accept();
                    client.configureBlocking(false);
                    System.out.println("接收到一个Accept操作");
                    client.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // 如果是读取事件，读取数据并打印到控制台
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead = client.read(buffer);
                    if (bytesRead == -1) {
                        // 如果读取到流的末尾，关闭连接
                        client.close();
                    } else {
                        // 将读取到的数据写回客户端
                        buffer.flip();
                        client.write(buffer);
                    }
                    System.out.println(new String(buffer.array()));
                }

                iter.remove();
            }
        }
    }
}
