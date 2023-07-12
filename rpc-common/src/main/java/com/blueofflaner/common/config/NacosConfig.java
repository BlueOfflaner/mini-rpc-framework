package com.blueofflaner.common.config;

import com.blueofflaner.common.register.RegisterCenter;
import com.blueofflaner.common.register.impl.NacosRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Slf4j
@Configuration
@EnableConfigurationProperties(NacosProperties.class)
public class NacosConfig {

    @Resource
    NacosProperties properties;

    @Bean
    public RegisterCenter registerCenter() {
        String host = properties.getHostAddress();
        Integer port = properties.getPort();
        RegisterCenter registerCenter = new NacosRegistry(host, port);
        return registerCenter;
    }
}
