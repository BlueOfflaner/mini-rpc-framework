package com.blueofflaner.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {

    Integer status;

    Object returnValue;

    String exceptionMsg;

    @Override
    public int getMessageType() {
        return RPC_RESPONSE;
    }

    public RpcResponseMessage success(Object returnValue) {
        this.setStatus(ResponseStatus.OK.code);
        this.setReturnValue(returnValue);
        return this;
    }

    public RpcResponseMessage fail(ResponseStatus status) {
        this.setStatus(status.code);
        this.setExceptionMsg(status.msg);
        return this;
    }
}
