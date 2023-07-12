package com.blueofflaner.server.rpc_server;

import com.blueofflaner.common.register.RegisterCenter;
import com.blueofflaner.common.util.ReflectUtils;
import com.blueofflaner.server.Server;
import com.blueofflaner.server.annotation.RpcService;
import com.blueofflaner.server.config.ServerProperties;
import com.blueofflaner.server.provider.ServiceProviderManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

@Slf4j
@Setter
@Getter
@ConditionalOnBean(RegisterCenter.class)
public abstract class RpcServer implements Server {

    protected Integer serializerCode;
    protected ApplicationContext context;
    protected final ServiceProviderManager serviceProviderManager = new ServiceProviderManager();
    protected RegisterCenter registerCenter;
    protected String localhost;
    protected int port;
    protected double weight = 1;

    public void register(String serviceName, Object service) {
        this.serviceProviderManager.addServiceProvider(serviceName, service);
        boolean b = registerCenter.register(serviceName, localhost, port);
        if(b)
            log.info(serviceName + " 已注册");
        else{
            log.warn(serviceName + " 注册失败");
        }
    }

    private void registerAll() {
        String packageName = context.getEnvironment().getProperty("rpc-server.base-package");
        if(null == packageName) {
            packageName = context.getBean(ServerProperties.class).getBasePackage();
        }
        registerCenter = context.getBean(RegisterCenter.class);
        if (localhost == null) {
            try {
                localhost = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {

            }
        }
        if (localhost == null) throw new RuntimeException("获取本机地址异常！");
        if (registerCenter == null) throw new RuntimeException("未配置注册中心");
        if (packageName == null) throw new RuntimeException("未配置base-package");

        log.info("扫描包 " + packageName + " 开始注册服务");

        Set<Class<?>> classes = ReflectUtils.getClassesByAnnotation(packageName, RpcService.class);
        for(Class<?> clazz : classes) {
            Object obj = null;

            obj = this.context.getBean(clazz);
            if(obj == null) {
                try {
                    obj = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(obj != null) {
                Class<?>[] interfaces = clazz.getInterfaces();
                for(Class<?> anInterface : interfaces) {
                    register(anInterface.getName(), obj);
                }
            }
        }
    }

    protected abstract void start();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public void run(ApplicationArguments args) {
        this.registerAll();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clearAllServices();
        }));
        this.start();
    }

    private void clearAllServices() {
        for (String serviceName : serviceProviderManager.getRegisteredService()) {
            boolean b = registerCenter.deRegister(serviceName, localhost, port);
            if(b) log.info(serviceName + " 已注销");
            else log.warn(serviceName + " 注销失败");
        }
        log.info("所有服务已注销");
    }
}
