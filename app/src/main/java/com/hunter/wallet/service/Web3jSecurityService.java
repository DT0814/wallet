package com.hunter.wallet.service;

import android.content.Context;

import java.util.List;

import lr.com.wallet.dao.ETHWalletDao;
import lr.com.wallet.pojo.ETHWalletResult;

public class Web3jSecurityService implements SecurityService {

    private Context context;

    public Web3jSecurityService(Context context) {
        this.context = context;
    }

    @Override
    public List<WalletInfo> getAllWallet() throws SecurityErrorException {
        ETHWalletResult data = ETHWalletDao.getAllWalletInfo();
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
        return (List<WalletInfo>) data.getData();
    }

    @Override
    public WalletInfo createWallet(String name, String password) throws SecurityErrorException {
        ETHWalletResult result = ETHWalletDao.createWallet(name, password);
        if (result.isError()) {
            throw new SecurityErrorException(result.getCode());
        }
        return (WalletInfo) result.getData();
    }

    @Override
    public void deleteWallet(int id, String password) throws SecurityErrorException {
        ETHWalletResult result = ETHWalletDao.deleteWallet(id, password);
        if (result.isError()) {
            throw new SecurityErrorException(result.getCode());
        }
    }

    @Override
    public WalletInfo recoverByMnemonic(String name, String password, String mnemonic, String path) throws SecurityErrorException {
        WalletInfo walletInfo = ETHWalletDao.recoverByMnemonic(name, password, mnemonic, path);
        if (null == walletInfo) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_MNEMONIC_TRANSFER_FAIL);
        }
        return walletInfo;
    }

    @Override
    public WalletInfo recoverByKeystore(String name, String password, String keystore) throws SecurityErrorException {
        WalletInfo walletInfo = ETHWalletDao.recoverByKeystore(name, password, keystore);
        if (null == walletInfo) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_KEYSTORE_RESOLVE_FAIL);
        }
        return walletInfo;
    }

    @Override
    public WalletInfo recoverByPrikey(String name, String password, byte[] prikey) throws SecurityErrorException {
        WalletInfo walletInfo = ETHWalletDao.recoverByPrikey(name, password, prikey);
        if (null == walletInfo) {
        }
        return walletInfo;
    }

    @Override
    public byte[] signature(int id, String password, byte[] data) throws SecurityErrorException {
        return new byte[0];
    }

    @Override
    public String getKeystore(int id, String password) throws SecurityErrorException {
        String keystore = ETHWalletDao.getKeystore(id, password);
        if (null == keystore) {

        }
        return keystore;
    }

    @Override
    public String getMnemonic(int id, String password) throws SecurityErrorException {
        String mnemonic = ETHWalletDao.getMnemonic(id, password);
        if (null == mnemonic) {

        }
        return mnemonic;
    }

    @Override
    public byte[] getPrikey(int id, String password) throws SecurityErrorException {
        byte[] prikey = ETHWalletDao.getPrikey(id, password);
        if (null == prikey) {

        }
        return prikey;
    }

    @Override
    public byte[] getPubkey(int id) throws SecurityErrorException {

        byte[] pubkey = ETHWalletDao.getPubkey(id);
        if (null == pubkey) {

        }
        return pubkey;
    }

    @Override
    public void changeName(int id, String newName) throws SecurityErrorException {

    }

    @Override
    public void changePassword(int id, String password, String newPassword) throws SecurityErrorException {

    }
}
