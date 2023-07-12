package com.blueofflaner.client.loadbalancer.impl;

import com.blueofflaner.client.loadbalancer.LoadBalancer;
import com.blueofflaner.common.register.RegisterCenter;
import com.blueofflaner.common.register.ServerInstance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {

    private AtomicInteger index;

    private RegisterCenter registerCenter;

    public RoundRobinLoadBalancer(RegisterCenter registerCenter) {
        this.index = new AtomicInteger();
        this.registerCenter = registerCenter;
    }

    @Override
    public InetSocketAddress select(String serviceName) {
        List<ServerInstance> instanceList = registerCenter.getService(serviceName);
        if(instanceList.size() == 1) return getAddress(instanceList.get(0));
        if(index.get() >= instanceList.size()) {
            index.set(index.get()%instanceList.size());
        }
        return getAddress(instanceList.get(index.getAndIncrement()));
    }

    private InetSocketAddress getAddress(ServerInstance instance) {
        return new InetSocketAddress(instance.getHost(), instance.getPort());
    }
}
