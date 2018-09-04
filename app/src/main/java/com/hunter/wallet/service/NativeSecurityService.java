package com.hunter.wallet.service;

import java.util.List;

public class NativeSecurityService {
    private static NativeSecurityService instance;

    static {
        System.loadLibrary("wsservice");
    }

    private NativeSecurityService() {
    }

    static NativeSecurityService getInstance() {
        if (instance == null) {
            instance = new NativeSecurityService();
        }
        return instance;
    }
    native List<EthWallet> getAllWallet() throws TeeErrorException;

    native EthWallet createWallet(String name, String password) throws TeeErrorException;

    native void deleteWallet(int id, String password) throws TeeErrorException;

    native EthWallet recoverByMnemonic(String name, String password, String mnemonic, String path) throws TeeErrorException;

    native EthWallet recoverByKeystore(String name, String password, String keystore) throws TeeErrorException;

    native EthWallet recoverByPrikey(String name, String password, byte[] prikey) throws TeeErrorException;

    native byte[] signature(int id, String password, byte[] data) throws TeeErrorException;

    native String getKeystore(int id, String password) throws TeeErrorException;

    native String getMnemonic(int id, String password) throws TeeErrorException;

    native byte[] getPrikey(int id, String password) throws TeeErrorException;

    native byte[] getPubkey(int id) throws TeeErrorException;

    native void changeName(int id, String newName) throws TeeErrorException;

    native void changePassword(int id, String password, String newPassword) throws TeeErrorException;

}
