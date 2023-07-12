package com.blueofflaner.client.config;

import com.blueofflaner.client.loadbalancer.LoadBalancer;
import com.blueofflaner.client.loadbalancer.impl.RandomLoadBalancer;
import com.blueofflaner.client.loadbalancer.impl.RoundRobinLoadBalancer;
import com.blueofflaner.client.rpc_client.RpcClient;
import com.blueofflaner.client.rpc_client.impl.NettyClient;
import com.blueofflaner.common.config.NacosConfig;
import com.blueofflaner.common.register.RegisterCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Slf4j
@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class ClientConfig {

    @Resource
    ClientProperties properties;

    @Bean
    @ConditionalOnBean(RegisterCenter.class)
    public LoadBalancer loadBalancer(RegisterCenter registerCenter) {
        String loadBalancerName = properties.getLoadBalancer();
        LoadBalancer loadBalancer = null;
        if(loadBalancerName.toLowerCase().equals("random")) {
            loadBalancer = new RandomLoadBalancer(registerCenter);
        }
        else if(loadBalancerName.toLowerCase().equals("round")) {
            loadBalancer = new RoundRobinLoadBalancer(registerCenter);
        }
        else {
            throw new IllegalArgumentException("负载均衡器必须为Random或RoundRobin");
        }
        log.info("负载均衡器注册: {}", loadBalancer);
        return loadBalancer;
    }

    @Bean
    public RpcClient rpcClient(LoadBalancer loadBalancer) {
        Integer serializerCode = properties.getSerializerCode();
        Integer redo = properties.getRedo();

        RpcClient client = new NettyClient(serializerCode, loadBalancer, redo);

        log.info("客户端注册: {}", client);
        return client;
    }
}
