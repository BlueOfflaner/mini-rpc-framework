package com.blueofflaner;

import static org.junit.Assert.assertTrue;

import com.blueofflaner.client.handler.RpcClientHandler;
import com.blueofflaner.client.loadbalancer.impl.RandomLoadBalancer;
import com.blueofflaner.client.rpc_client.impl.NettyClient;
import com.blueofflaner.common.register.impl.NacosRegistry;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.DefaultPromise;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void test() {
        //NettyClient client = new NettyClient(1, new RandomLoadBalancer(new NacosRegistry()));
        //client.getTaskMap().put("1", null);
        //RpcClientHandler handler = new RpcClientHandler(client.getTaskMap());
        //client.getTaskMap().put("2", null);
        //handler.getTaskMap().put("3", null);
        //
        //System.out.println(client.getTaskMap());
        //client.getTaskMap().remove("1");
        //System.out.println(handler.getTaskMap());
    }
}
