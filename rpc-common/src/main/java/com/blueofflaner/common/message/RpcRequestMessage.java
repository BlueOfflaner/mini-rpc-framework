package com.blueofflaner.common.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class RpcRequestMessage extends Message {
    @JsonProperty("interfaceName")
    private String interfaceName;

    @JsonProperty("methodName")
    private String methodName;

    @JsonProperty("paramTypes")
    private Class[] paramTypes;

    @JsonProperty("params")
    private Object[] params;

    @JsonProperty("returnType")
    private Class<?> returnType;

    @JsonProperty("heartBeat")
    private Boolean isHeartBeat = false;

    public RpcRequestMessage(String sequenceId) {
        super.setSequenceId(sequenceId);
    }

    public RpcRequestMessage(String sequenceId, String interfaceName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.paramTypes = parameterTypes;
        this.params = parameterValue;
        this.isHeartBeat = false;
    }

    public RpcRequestMessage sendHeartBeat(String sequenceId) {
        RpcRequestMessage requestMessage = new RpcRequestMessage(sequenceId);
        this.isHeartBeat = true;
        return requestMessage;
    }

    public boolean isHeartBeat() {
        return isHeartBeat;
    }

    @Override
    public int getMessageType() {
        return RPC_REQUEST;
    }
}
