package com.blueofflaner.common.message;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message {

    public static Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();


    public static Class<? extends Message> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private String sequenceId;

    private int messageType;

    protected static int RPC_REQUEST = 10;

    protected static int RPC_RESPONSE = 11;

    static {
        messageClasses.put(RPC_REQUEST, RpcRequestMessage.class);
        messageClasses.put(RPC_RESPONSE, RpcResponseMessage.class);
    }

    abstract public int getMessageType();
}
