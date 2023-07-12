package com.blueofflaner.test.client;

import com.blueofflaner.client.rpc_client.RpcClient;
import com.blueofflaner.client.rpc_client.RpcClientProxy;
import com.blueofflaner.rpc.api.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestClient {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        RpcClient rpcClient = applicationContext.getBean(RpcClient.class);
        RpcClientProxy proxy = new RpcClientProxy(rpcClient);
        HelloService proxyService = proxy.getProxyService(HelloService.class);

        String name = "xx";
        int age = 13;
        String hello = proxyService.hello(name, 13);
        System.out.println(hello);

        String bye = proxyService.Bye("zhan");
        System.out.println(bye);

        System.out.println(proxyService.Bye("name, 11"));
        System.out.println(proxyService.Bye("name, 9"));
    }

}
