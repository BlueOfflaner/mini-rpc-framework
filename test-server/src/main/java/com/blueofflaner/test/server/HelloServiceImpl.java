package com.blueofflaner.test.server;

import com.blueofflaner.rpc.api.HelloService;
import com.blueofflaner.server.annotation.RpcService;

@RpcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name, int age) {
        String res = String.format("Hello %s, %s old", name, age);
        //int i = 1/0;
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String Bye(String name) {
        String res = "Good Bye " + name;
        return res;
    }
}
