package com.blueofflaner.test.server;

import com.alibaba.nacos.common.JustForTest;
import com.blueofflaner.common.message.RpcRequestMessage;
import com.blueofflaner.common.serializer.SerializerMethod;
import com.blueofflaner.common.util.SequenceIdGenerator;
import com.blueofflaner.server.rpc_server.RpcServer;
import com.blueofflaner.server.rpc_server.impl.NettyServer;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.charset.StandardCharsets;

@AutoConfiguration
public class TestServer {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        RpcServer rpcServer = applicationContext.getBean(RpcServer.class);
        rpcServer.run(null);
    }

    @Test
    public void test() {
        SerializerMethod serializerMethod = SerializerMethod.JSON;
        RpcRequestMessage message = new RpcRequestMessage(SequenceIdGenerator.getId());
        byte[] bytes = serializerMethod.serialize(message);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
        RpcRequestMessage message1 = serializerMethod.deserialize(RpcRequestMessage.class, bytes);
        System.out.println(message1);
    }
}
