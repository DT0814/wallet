package com.hunter.wallet.service;

import android.util.Log;

import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.util.Arrays;

public class WalletInfo {

    private int id;
    private String name;
    private byte[] pubkey;
    private boolean hasLock;
    private int failTimes;


    @Override
    public String toString() {
        return "WalletInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pubkey=" + Arrays.toString(pubkey) +
                ", hasLock=" + hasLock +
                ", failTimes=" + failTimes +
                '}';
    }

    public WalletInfo() {
    }

    public WalletInfo(int id, String name, byte[] pubkey, boolean hasLock, int failTimes) {
        this.id = id;
        this.name = name;
        this.pubkey = pubkey;
        this.hasLock = hasLock;
        this.failTimes = failTimes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPubkey() {
        return pubkey;
    }

    public void setPubkey(byte[] pubkey) {
        this.pubkey = pubkey;
    }

    public boolean isHasLock() {
        return hasLock;
    }

    public void setHasLock(boolean hasLock) {
        this.hasLock = hasLock;
    }

    public int getFailTimes() {
        return failTimes;
    }

    public void setFailTimes(int failTimes) {
        this.failTimes = failTimes;
    }

    public String getAddr() {
        StringBuffer sb = new StringBuffer();
        for (byte b : pubkey) {
            sb.append(b);
        }
        Log.i("pubkey", sb.toString());
        return Numeric.toHexString(Keys.getAddress(pubkey));
    }
}
