package com.hunter.wallet.service;

import java.util.Arrays;

public class UserInfo {
    private boolean hasInit;
    private boolean pinHasLock;
    private int pinFailTimes;
    private String bindMobile;
    private byte[] deviceId;
    private byte[] authCode;

    @Override
    public String toString() {
        return "UserInfo{" +
                "hasInit=" + hasInit +
                ", pinHasLock=" + pinHasLock +
                ", pinFailTimes=" + pinFailTimes +
                ", bindMobile='" + bindMobile + '\'' +
                ", deviceId=" + Arrays.toString(deviceId) +
                ", authCode=" + Arrays.toString(authCode) +
                '}';
    }

    public UserInfo(boolean hasInit, boolean pinHasLock, int pinFailTimes, String bindMobile, byte[] deviceId, byte[] authCode) {
        this.hasInit = hasInit;
        this.pinHasLock = pinHasLock;
        this.pinFailTimes = pinFailTimes;
        this.bindMobile = bindMobile;
        this.deviceId = deviceId;
        this.authCode = authCode;
    }

    public boolean isHasInit() {
        return hasInit;
    }

    public void setHasInit(boolean hasInit) {
        this.hasInit = hasInit;
    }

    public boolean isPinHasLock() {
        return pinHasLock;
    }

    public void setPinHasLock(boolean pinHasLock) {
        this.pinHasLock = pinHasLock;
    }

    public int getPinFailTimes() {
        return pinFailTimes;
    }

    public void setPinFailTimes(int pinFailTimes) {
        this.pinFailTimes = pinFailTimes;
    }

    public String getBindMobile() {
        return bindMobile;
    }

    public void setBindMobile(String bindMobile) {
        this.bindMobile = bindMobile;
    }

    public byte[] getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(byte[] deviceId) {
        this.deviceId = deviceId;
    }

    public byte[] getAuthCode() {
        return authCode;
    }

    public void setAuthCode(byte[] authCode) {
        this.authCode = authCode;
    }
}
