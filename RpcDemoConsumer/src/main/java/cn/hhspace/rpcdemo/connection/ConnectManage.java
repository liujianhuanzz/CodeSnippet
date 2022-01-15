package cn.hhspace.rpcdemo.connection;

import cn.hhspace.rpcdemo.netty.NettyClient;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/15 1:52 下午
 * @Package: cn.hhspace.rpcdemo.connection
 */
@Component
public class ConnectManage {

    @Autowired
    NettyClient nettyClient;

    private AtomicInteger roundRobin = new AtomicInteger(0);
    private CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();
    private Map<SocketAddress, Channel> channelNodes = new ConcurrentHashMap<>();

    public Channel chooseChannel() {
        if (channels.size() > 0 ) {
            int size = channels.size();
            int index = (roundRobin.getAndAdd(1) + size) % size;
            return channels.get(index);
        } else {
            return null;
        }
    }

    public synchronized void updateConnectServer(List<String> addressList) {
        if (addressList == null || addressList.size() == 0) {
            System.out.println("没有可用的服务器节点，全部服务节点已关闭");
            for (final Channel channel : channels) {
                SocketAddress socketAddress = channel.remoteAddress();
                Channel channel1 = channelNodes.get(socketAddress);
                channel1.close();
            }
            channels.clear();
            channelNodes.clear();
            return;
        }

        HashSet<SocketAddress> newAllServeNodeSet = new HashSet<>();
        for (int i=0; i < addressList.size(); i++) {
            String[] array = addressList.get(i).split(":");
            if (array.length == 2) {
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
                newAllServeNodeSet.add(inetSocketAddress);
            }
        }

        for (SocketAddress serveNodeAddress : newAllServeNodeSet) {
            Channel channel = channelNodes.get(serveNodeAddress);
            if (channel != null && channel.isOpen()) {
                System.out.println("当前服务节点已存在，无需重新连接：" + serveNodeAddress);
            } else {
                connectServerNode(serveNodeAddress);
            }
        }

        for (int i=0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            SocketAddress socketAddress = channel.remoteAddress();
            if (!newAllServeNodeSet.contains(socketAddress)) {
                System.out.println("删除失效服务节点：" + socketAddress);
                removeChannel(channel);
            }
        }
    }

    private void connectServerNode(SocketAddress address) {
        try {
            Channel channel = nettyClient.doConnect(address);
            addChannel(channel, address);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void addChannel(Channel channel, SocketAddress address) {
        System.out.println("加入Channel到连接管理器：" + address);
        channels.add(channel);
        channelNodes.put(address, channel);
    }

    public void removeChannel(Channel channel) {
        System.out.println("从连接管理器中移除失效Channel: " + channel.remoteAddress());
        Channel channel1 = channelNodes.get(channel.remoteAddress());
        if ( channel1 != null) {
            channel1.close();
        }
        channels.remove(channel);
        channelNodes.remove(channel.remoteAddress());
    }
}
