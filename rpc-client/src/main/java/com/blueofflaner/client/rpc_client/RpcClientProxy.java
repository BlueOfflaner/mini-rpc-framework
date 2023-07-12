package com.blueofflaner.client.rpc_client;

import com.blueofflaner.common.message.RpcRequestMessage;
import com.blueofflaner.common.message.RpcResponseMessage;
import com.blueofflaner.common.util.SequenceIdGenerator;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

@Slf4j
public class RpcClientProxy {
    private final RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public <T> T getProxyService(Class<T> serviceClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setClassLoader(serviceClass.getClassLoader());
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                log.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
                RpcRequestMessage msg = new RpcRequestMessage(
                        SequenceIdGenerator.getId(),
                        serviceClass.getName(),
                        method.getName(),
                        method.getReturnType(),
                        method.getParameterTypes(),
                        objects
                );
                log.info("{}", msg);

                Object responseVal = rpcClient.send(msg);
                return responseVal;
            }
        });

        return (T) enhancer.create();
    }
}
