package com.blueofflaner.common.message;

public enum ResponseStatus {
    OK(200, "执行成功"),
    NO_SUCH_METHOD(404, "该方法不存在"),
    INVALID_PARAMETERS(400, "无效参数"),
    INVOKE_FAIL(500, "执行失败");

    public int code;

    public String msg;

    ResponseStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
