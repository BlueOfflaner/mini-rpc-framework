package com.blueofflaner.client.handler;

import com.blueofflaner.common.message.ResponseStatus;
import com.blueofflaner.common.message.RpcRequestMessage;
import com.blueofflaner.common.message.RpcResponseMessage;
import com.blueofflaner.common.util.SequenceIdGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Setter
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    private final ConcurrentHashMap<String, Promise<Object>> unprocessedResponsePromises;

    public RpcClientHandler(ConcurrentHashMap<String, Promise<Object>> unprocessedResponsePromises) {
        this.unprocessedResponsePromises = unprocessedResponsePromises;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        Promise<Object> promise = unprocessedResponsePromises.get(msg.getSequenceId());
        if(null != promise) {
            Integer status = msg.getStatus();
            if(status.equals(ResponseStatus.OK.code)) {
                promise.trySuccess(msg.getReturnValue());
            }
            else {
                promise.tryFailure(new RuntimeException(msg.getExceptionMsg()));
            }
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                RpcRequestMessage rpcRequest = new RpcRequestMessage(SequenceIdGenerator.getId());
                rpcRequest.setIsHeartBeat(true);
                ctx.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
