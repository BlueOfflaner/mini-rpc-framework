package com.blueofflaner.client.rpc_client;

import com.blueofflaner.common.message.RpcRequestMessage;

public interface RpcClient {

    Object send(RpcRequestMessage request);

    void close();
}
