package com.blueofflaner.server;

import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public interface Server extends ApplicationContextAware {
    void register(String serviceName, Object service);

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
