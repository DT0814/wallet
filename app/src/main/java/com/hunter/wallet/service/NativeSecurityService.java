package com.hunter.wallet.service;

import java.util.List;

public class NativeSecurityService implements SecurityService {

    public native List<WalletInfo> getAllWallet() throws SecurityErrorException;

    public native WalletInfo createWallet(String name, String password) throws SecurityErrorException;

    public native void deleteWallet(int id, String password) throws SecurityErrorException;

    public native WalletInfo recoverByMnemonic(String name, String password, String mnemonic, String path) throws SecurityErrorException;

    public native WalletInfo recoverByKeystore(String name, String password, String keystore, String ksPwd) throws SecurityErrorException;

    public native WalletInfo recoverByPrikey(String name, String password, byte[] prikey) throws SecurityErrorException;

    public native byte[] signature(int id, String password, byte[] data) throws SecurityErrorException;

    public native String getKeystore(int id, String password) throws SecurityErrorException;

    public native String getMnemonic(int id, String password) throws SecurityErrorException;

    public native byte[] getPrikey(int id, String password) throws SecurityErrorException;

    public native byte[] getPubkey(int id) throws SecurityErrorException;

    public native void changeName(int id, String newName) throws SecurityErrorException;

    public native void changePassword(int id, String password, String newPassword) throws SecurityErrorException;

    public native UserInfo getUserInfo() throws SecurityErrorException;

    public native void userInit(byte[] pin, String mobile, byte[] signature) throws SecurityErrorException;

    public native void changePin(byte[] pin, byte[] newPin) throws SecurityErrorException;

    public native void rebindMobile(byte[] pin, String newMobile, byte[] signature) throws SecurityErrorException;

    public native void unlockWallet(int id, byte[] pin) throws SecurityErrorException;

    public native void unlockPin(byte[] signature) throws SecurityErrorException;

    public native void resetWallet(byte[] pin, byte[] signature) throws SecurityErrorException;
}
