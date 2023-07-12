package com.blueofflaner.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Random;

@ConfigurationProperties(prefix = "rpc-server")
@Getter
@Setter
public class ServerProperties {
    static Random random = new Random();
    String hostAddress = "localhost";
    Integer port = random.nextInt(9999) + 10000;
    Integer serializerCode = 1;
    String basePackage = "com.blueofflaner";
    Double weight = 1.0;
}
