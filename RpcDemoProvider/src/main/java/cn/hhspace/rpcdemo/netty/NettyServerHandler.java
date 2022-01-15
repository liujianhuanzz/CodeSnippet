package cn.hhspace.rpcdemo.netty;

import cn.hhspace.rpcdemo.entity.Request;
import cn.hhspace.rpcdemo.entity.Response;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/14 4:08 下午
 * @Package: cn.hhspace.rpcdemo.netty
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Map<String, Object> serviceMap;

    public NettyServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端连接成功： " + ctx.channel().remoteAddress());
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("客户端断开连接： " + ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Request request = JSON.parseObject(msg.toString(), Request.class);

        if ("heartBeat".equals(request.getMethodName())) {
            System.out.println("客户端心跳信息: " + ctx.channel().remoteAddress());
        } else {
            System.out.println("RPC客户端请求信息：" + request.getClassName() + " 方法名：" + request.getMethodName());
            Response response = new Response();
            response.setRequestId(request.getId());
            try {
                Object o = this.handler(request);
                response.setData(o);
            } catch (Throwable e) {
                e.printStackTrace();
                response.setCode(1);
                response.setErrorMsg(e.toString());
            }
            ctx.writeAndFlush(response);
        }
    }

    /**
     * 通过反射执行本地方法
     * @param request
     * @return
     * @throws Throwable
     */
    private Object handler(Request request) throws Exception {
        String className = request.getClassName();
        Object serviceBean = serviceMap.get(className);

        if (serviceBean != null) {
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, getParameters(parameterTypes,parameters));
        } else {
            throw new Exception("未找到服务接口,请检查配置!:"+className+"#"+request.getMethodName());
        }
    }

    /**
     * 获取参数列表
     * @param parameterTypes
     * @param parameters
     * @return
     */
    private Object[] getParameters(Class<?>[] parameterTypes, Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return parameters;
        } else {
            Object[] newParameters = new Object[parameters.length];
            for (int i=0; i < parameters.length; i++) {
                newParameters[i] = JSON.parseObject(parameters[i].toString(), parameterTypes[i]);
            }
            return newParameters;
        }
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("客户端已超过60秒未读写数据，关闭连接： " + ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println(cause.getMessage());
        ctx.close();
    }
}
