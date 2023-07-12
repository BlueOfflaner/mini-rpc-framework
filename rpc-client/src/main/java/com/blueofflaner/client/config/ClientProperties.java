package com.blueofflaner.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rpc-client")
public class ClientProperties {
    Integer serializerCode = 1;
    String basePackage;
    String loadBalancer = "Random";
    Integer redo = 3;
}
