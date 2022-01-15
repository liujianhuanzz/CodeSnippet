package cn.hhspace.rpcdemo.codec.json;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/15 11:11 上午
 * @Package: cn.hhspace.rpcdemo.netty.codec.json
 */
public class JSONEncoder extends MessageToMessageEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List list) throws Exception {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = JSON.toJSONBytes(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        list.add(byteBuf);
    }
}
