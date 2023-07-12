package com.blueofflaner.client.loadbalancer;

import java.net.InetSocketAddress;

public interface LoadBalancer {
    InetSocketAddress select(String serviceName);
}
