package com.blueofflaner.server.config;

import com.blueofflaner.common.config.NacosConfig;
import com.blueofflaner.common.config.NacosProperties;
import com.blueofflaner.server.rpc_server.RpcServer;
import com.blueofflaner.server.rpc_server.impl.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Slf4j
@Configuration
@EnableConfigurationProperties(ServerProperties.class)
public class ServerConfig {
    @Resource
    ServerProperties properties;

    @Bean
    public RpcServer rpcServer() {
        String hostAddress = properties.getHostAddress();
        Integer port = properties.getPort();
        Integer serializerCode = properties.getSerializerCode();
        Double weight = properties.getWeight();

        RpcServer server = new NettyServer();
        server.setLocalhost(hostAddress);
        server.setPort(port);
        server.setSerializerCode(serializerCode);
        server.setWeight(weight);
        log.info("新注册服务器: {}", server);
        return server;
    }
}
