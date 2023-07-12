package com.blueofflaner.server.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Setter
public class ServiceProviderManager {

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    public <T> void addServiceProvider(String serviceName, T service) {
        if(serviceMap.containsKey(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null) {
            throw new RuntimeException("找不到该服务");
        }
        return service;
    }
}
