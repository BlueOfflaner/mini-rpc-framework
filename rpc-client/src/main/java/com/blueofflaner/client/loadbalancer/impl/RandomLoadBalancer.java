package com.blueofflaner.client.loadbalancer.impl;

import com.blueofflaner.client.loadbalancer.LoadBalancer;
import com.blueofflaner.common.register.RegisterCenter;
import com.blueofflaner.common.register.ServerInstance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {

    private Random random;

    private RegisterCenter registerCenter;

    public RandomLoadBalancer(RegisterCenter registerCenter) {
        this.random = new Random();
        this.registerCenter = registerCenter;
    }

    @Override
    public InetSocketAddress select(String serviceName) {
        List<ServerInstance> instanceList = registerCenter.getService(serviceName);
        if(instanceList.size() == 1) return getAddress(instanceList.get(0));
        int totalWeight = 0;
        for (ServerInstance instance : instanceList) {
            totalWeight += instance.getWeight();
        }
        int i = random.nextInt(totalWeight);
        ServerInstance instance = null;
        for (ServerInstance serverInstance : instanceList) {
            if(i < serverInstance.getWeight()){
                instance = serverInstance;
                break;
            }
            i -= serverInstance.getWeight();
        }
        return getAddress(instance);
    }

    private InetSocketAddress getAddress(ServerInstance instance) {
        return new InetSocketAddress(instance.getHost(), instance.getPort());
    }
}
