package com.blueofflaner.server.rpc_server.impl;

import com.blueofflaner.common.handler.MessageCodec;
import com.blueofflaner.common.handler.ProtocolFrameDecoder;
import com.blueofflaner.common.serializer.Serializer;
import com.blueofflaner.common.serializer.SerializerMethod;
import com.blueofflaner.server.handler.RpcServerHandler;
import com.blueofflaner.server.rpc_server.RpcServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Slf4j
//服务器端主体代码
public class NettyServer extends RpcServer {
    @Override
    protected void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
                                    .addLast(new LoggingHandler("Logger1"))
                                    .addLast(new ProtocolFrameDecoder())
                                    .addLast(new MessageCodec(serializerCode))
                                    .addLast(new LoggingHandler("Logger2"))
                                    .addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new RpcServerHandler(serviceProviderManager.getServiceMap()));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(this.port).sync();
            log.info("Netty 服务器已启动, 端口: " + port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
