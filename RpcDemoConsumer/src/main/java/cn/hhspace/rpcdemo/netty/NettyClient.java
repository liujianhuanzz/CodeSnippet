package cn.hhspace.rpcdemo.netty;

import cn.hhspace.rpcdemo.codec.json.JSONDecoder;
import cn.hhspace.rpcdemo.codec.json.JSONEncoder;
import cn.hhspace.rpcdemo.connection.ConnectManage;
import cn.hhspace.rpcdemo.entity.Request;
import cn.hhspace.rpcdemo.entity.Response;
import com.alibaba.fastjson.JSONArray;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/15 1:49 下午
 * @Package: cn.hhspace.rpcdemo.netty
 */
@Component
public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup(1);
    private Bootstrap bootstrap = new Bootstrap();

    @Autowired
    NettyClientHandler handler;

    @Autowired
    ConnectManage connectManage;

    public NettyClient () {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    //创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 30));
                        pipeline.addLast(new JSONEncoder());
                        pipeline.addLast(new JSONDecoder());
                        pipeline.addLast("handler",handler);
                    }
                });

    }

    @PreDestroy
    public void destroy() {
        System.out.println("RPC客户端退出，释放资源");
        group.shutdownGracefully();
    }

    public Object send(Request request) throws InterruptedException {
        Channel channel = connectManage.chooseChannel();
        if (channel != null && channel.isOpen()) {
            SynchronousQueue<Object> queue = handler.sendRequest(request, channel);
            Object result = queue.take();
            return JSONArray.toJSONString(result);
        } else {
            Response res = new Response();
            res.setCode(1);
            res.setErrorMsg("未正确连接到服务器.请检查相关配置信息!");
            return JSONArray.toJSONString(res);
        }
    }

    public Channel doConnect(SocketAddress address) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(address);
        Channel channel = channelFuture.sync().channel();
        return channel;
    }
}
