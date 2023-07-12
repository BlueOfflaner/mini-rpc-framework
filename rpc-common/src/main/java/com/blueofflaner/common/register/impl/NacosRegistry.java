package com.blueofflaner.common.register.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.blueofflaner.common.register.RegisterCenter;
import com.blueofflaner.common.register.ServerInstance;

import java.util.ArrayList;
import java.util.List;

public class NacosRegistry implements RegisterCenter {

    private final double DEFAULT_WEIGHT = 1;

    NamingService namingService;

    public NacosRegistry(String host, int port) {
        try {
            namingService = NamingFactory.createNamingService(host + ":" + port);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ServerInstance> getService(String serviceName) {
        List<ServerInstance> serverInstances = new ArrayList<>();
        try {
            List<Instance> instanceList = namingService.getAllInstances(serviceName);
            for(Instance instance : instanceList) {
                serverInstances.add(new ServerInstance(instance.getIp(), instance.getPort(), instance.getWeight()));
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return serverInstances;
    }

    @Override
    public boolean register(String serviceName, String host, int port, double weight) {
        Instance instance = new Instance();
        instance.setServiceName(serviceName);
        instance.setIp(host);
        instance.setPort(port);
        instance.setWeight(weight);

        try {
            namingService.registerInstance(serviceName, instance);
            return true;
        } catch (NacosException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean register(String serviceName, String host, int port) {
        return register(serviceName, host, port, DEFAULT_WEIGHT);
    }

    @Override
    public boolean deRegister(String serviceName, String host, int port) {
        try {
            namingService.deregisterInstance(serviceName, host, port);
            return true;
        } catch (NacosException e) {
            e.printStackTrace();
            return false;
        }
    }
}
