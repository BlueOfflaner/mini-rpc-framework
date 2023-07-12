package com.blueofflaner.server.handler;

import com.blueofflaner.common.message.ResponseStatus;
import com.blueofflaner.common.message.RpcRequestMessage;
import com.blueofflaner.common.message.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    Map<String, Object> services;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("连接 " + ctx.channel().remoteAddress().toString() + " 已建立");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("连接 " + ctx.channel().remoteAddress().toString() + " 已断开");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage requestMessage) throws Exception {
        if(requestMessage.isHeartBeat()) {
            return;
        }
        log.info("收到消息: {}", requestMessage);
        RpcResponseMessage response = doRequest(requestMessage);
        ctx.channel().writeAndFlush(response);
    }

    private RpcResponseMessage doRequest(RpcRequestMessage request) {
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        Object[] params = request.getParams();
        Object service = this.services.get(interfaceName);

        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(request.getSequenceId());
        try {
            Method method = service.getClass().getMethod(methodName, paramTypes);
            Object returnValue = method.invoke(service, params);
            return response.success(returnValue);
        } catch (NoSuchMethodException e) {
            log.error("方法 " + methodName + " 不存在！");
            e.printStackTrace();
            return response.fail(ResponseStatus.NO_SUCH_METHOD);
        } catch (InvocationTargetException | IllegalAccessException e ) {
            log.error("方法 " + methodName + " 执行失败！");
            e.printStackTrace();
            return response.fail(ResponseStatus.INVOKE_FAIL);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包，断开连接...");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public RpcServerHandler(Map<String, Object> services) {
        this.services = services;
    }
}
