package com.hunter.wallet.service;

import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.util.Arrays;

public class EthWallet {
    private String name;
    private int id;
    private String balance;
    private double num;
    private byte[] pubkey;

    @Override
    public String toString() {
        return "EthWallet{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", balance='" + balance + '\'' +
                ", num=" + num +
                ", pubkey=" + Arrays.toString(pubkey) +
                ", address=" + getAddr() +
                '}';
    }

    public EthWallet() {
    }

    public EthWallet(EthWallet ethWallet) {
        this.name = ethWallet.name;
        this.id = ethWallet.id;
        this.balance = ethWallet.balance;
        this.num = ethWallet.num;
        this.pubkey = ethWallet.pubkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public byte[] getPubkey() {
        return pubkey;
    }

    public void setPubkey(byte[] pubkey) {
        this.pubkey = pubkey;
    }

    public String getAddr() {
        return Numeric.toHexString(Keys.getAddress(pubkey));
    }
}
