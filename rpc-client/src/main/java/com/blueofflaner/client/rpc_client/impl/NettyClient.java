package com.blueofflaner.client.rpc_client.impl;

import com.blueofflaner.client.handler.RpcClientHandler;
import com.blueofflaner.client.loadbalancer.LoadBalancer;
import com.blueofflaner.client.rpc_client.RpcClient;
import com.blueofflaner.common.handler.MessageCodec;
import com.blueofflaner.common.handler.ProtocolFrameDecoder;
import com.blueofflaner.common.message.RpcRequestMessage;
import com.blueofflaner.common.util.SequenceIdGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

@Slf4j
@Getter
@Setter
//TODO 客户端主体代码
public class NettyClient implements RpcClient {

    private Integer serializerCode;
    private LoadBalancer loadBalancer;
    private ConcurrentHashMap<InetSocketAddress, Channel> channelMap;
    private ConcurrentHashMap<String, Promise<Object>> unprocessedResponsePromises;
    private Integer redo;
    private Bootstrap bootstrap;
    private NioEventLoopGroup group;

    public NettyClient(Integer serializerCode, LoadBalancer loadBalancer, Integer redo) {
        this.serializerCode = serializerCode;
        this.loadBalancer = loadBalancer;
        this.redo = redo;
        channelMap = new ConcurrentHashMap<>();
        unprocessedResponsePromises = new ConcurrentHashMap<>();
        this.init();
    }

    public void init() {
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                //.addLast(new LoggingHandler())
                                .addLast(new ProtocolFrameDecoder())
                                .addLast(new MessageCodec(serializerCode))
                                .addLast(new IdleStateHandler(0, 20, 0, TimeUnit.SECONDS))
                                .addLast(new RpcClientHandler(unprocessedResponsePromises));
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
    }

    @Override
    public Object send(RpcRequestMessage request) {
        return send(request, redo);
    }

    public Object send(RpcRequestMessage request, int count) {
        if(count < 0) return null;
        InetSocketAddress address = loadBalancer.select(request.getInterfaceName());
        Channel channel = getChannel(address, count);
        addUnprocessedResponse(request, channel);
        Object responseVal = doSend(request, channel);
        if(null == responseVal) {
            //channelMap.remove(address);
            log.warn("客户端发送消息失败!, 重试中，剩余次数: " + count);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return send(request, count - 1);
        }
        removeUnprocessedResponse(request);
        return responseVal;
    }

    private Object doSend(RpcRequestMessage request, Channel channel) {
        if(null != channel) {
            Promise<Object> promise = unprocessedResponsePromises.get(request.getSequenceId());
            channel.writeAndFlush(request)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if(channelFuture.isSuccess()) {
                                log.info("传输成功 " + request);
                            }
                            else {
                                log.info("传输失败 " + request);
                            }
                        }
                    });

            //TODO Debug
            try {
                promise.get(5, TimeUnit.SECONDS);
                if(promise.isSuccess()) return promise.getNow();
                else return null;
            } catch (TimeoutException e) {
                log.warn("消息超时!");
                return null;
            } catch (Exception e) {
                log.warn("执行失败!");
                channel.close();
            }
        }
        return null;
    }

    private Channel getChannel(InetSocketAddress address, int count) {
        if(count < 0) return null;
        Channel channel = channelMap.get(address);
        if(null == channel || !channel.isActive() || !channel.isOpen()) {
            try {
                channel = bootstrap.connect(address).sync().channel();
                channelMap.put(address, channel);
            } catch (InterruptedException e) {
                log.warn("客户端连接失败，重试中");
                channelMap.remove(address);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                return getChannel(address, count - 1);
            }
        }
        return channel;
    }

    private void addUnprocessedResponse(RpcRequestMessage request, Channel channel) {
        if(request.getSequenceId() == null) {
            request.setSequenceId(SequenceIdGenerator.getId());
        }
        if(!unprocessedResponsePromises.containsKey(request.getSequenceId())) {
            unprocessedResponsePromises.put(request.getSequenceId(), new DefaultPromise<>(channel.eventLoop()));
        }
    }

    private void removeUnprocessedResponse(RpcRequestMessage request) {
        unprocessedResponsePromises.remove(request.getSequenceId());
    }

    public void close() {
    }
}
