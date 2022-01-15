package cn.hhspace.rpcdemo.codec.json;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/15 11:14 上午
 * @Package: cn.hhspace.rpcdemo.netty.codec.json
 */
public class JSONDecoder extends LengthFieldBasedFrameDecoder {

    public JSONDecoder() {
        super(65535, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decode = (ByteBuf) super.decode(ctx, in);

        if (decode == null) {
            return null;
        }

        int i = decode.readableBytes();
        byte[] bytes = new byte[i];
        decode.readBytes(bytes);
        Object parse = JSON.parse(bytes);
        return parse;
    }
}
