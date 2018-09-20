package com.hunter.wallet.service;

import java.io.Serializable;

public class InvokeResult<T> implements Serializable {

    public static final int INVAKE_SUCCESS = 0x00000000;
    public static final int INVAKE_FAIL = 0xFFFF0001;
    public static final int INVAKE_EXCEPTION = 0xFFFF0002;

    private int code;
    private T data;
    private String msg;

    public InvokeResult() {
    }

    public InvokeResult(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSuccess() {
        return code == INVAKE_SUCCESS;
    }
}
