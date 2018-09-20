package com.hunter.wallet.service;

import android.content.Context;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
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
        ETHWalletResult data = ETHWalletDao.recoverByMnemonic(name, password, mnemonic, path);
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
        return (WalletInfo) data.getData();
    }

    @Override
    public WalletInfo recoverByKeystore(String name, String password, String keystore) throws SecurityErrorException {
        ETHWalletResult data = ETHWalletDao.recoverByKeystore(name, password, keystore);
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
        return (WalletInfo) data.getData();
    }

    @Override
    public WalletInfo recoverByPrikey(String name, String password, byte[] prikey) throws SecurityErrorException {

        ETHWalletResult data = ETHWalletDao.recoverByPrikey(name, password, prikey);
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
        return (WalletInfo) data.getData();
    }

    @Override
    public byte[] signature(int id, String password, byte[] data) throws SecurityErrorException {
        ETHWalletResult result = ETHWalletDao.getPrikey(id, password);
        if (result.isError()) {
            throw new SecurityErrorException(result.getCode());
        }
        ECKeyPair ecKeyPair = ECKeyPair.create((byte[]) result.getData());
        ECDSASignature sig = ecKeyPair.sign(data);
        byte[] r = sig.r.toByteArray();
        byte[] s = sig.s.toByteArray();
        byte[] ret = new byte[r.length + s.length + 2];
        ret[0] = (byte) r.length;
        System.arraycopy(r, 0, ret, 1, r.length);
        ret[r.length + 1] = (byte) s.length;
        System.arraycopy(s, 0, ret, r.length + 2, s.length);
        return ret;
    }

    @Override
    public String getKeystore(int id, String password) throws SecurityErrorException {
        ETHWalletResult data = ETHWalletDao.getKeystore(id, password);
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
        return (String) data.getData();
    }

    @Override
    public String getMnemonic(int id, String password) throws SecurityErrorException {
        ETHWalletResult data = ETHWalletDao.getMnemonic(id, password);
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
        return (String) data.getData();
    }

    @Override
    public byte[] getPrikey(int id, String password) throws SecurityErrorException {
        ETHWalletResult data = ETHWalletDao.getPrikey(id, password);
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
        return (byte[]) data.getData();
    }

    @Override
    public byte[] getPubkey(int id) throws SecurityErrorException {
        byte[] pubkey = ETHWalletDao.getPubkey(id);
        if (null == pubkey) {
            throw new SecurityErrorException(0xFFFFFFFF);
        }
        return pubkey;
    }

    @Override
    public void changeName(int id, String newName) throws SecurityErrorException {
        ETHWalletResult data = ETHWalletDao.changeName(id, newName);
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
    }

    @Override
    public void changePassword(int id, String password, String newPassword) throws SecurityErrorException {
        ETHWalletResult data = ETHWalletDao.changePassword(id, password, newPassword);
        if (data.isError()) {
            throw new SecurityErrorException(data.getCode());
        }
    }

    @Override
    public UserInfo getUserInfo() throws SecurityErrorException {
        return null;
    }

    @Override
    public void userInit(byte[] pin, String mobile, byte[] signature) throws SecurityErrorException {

    }

    @Override
    public void changePin(byte[] pin, byte[] newPin) throws SecurityErrorException {

    }

    @Override
    public void rebindMobile(byte[] pin, String newMobile, byte[] signature) throws SecurityErrorException {

    }

    @Override
    public void unlockWallet(byte[] signature) throws SecurityErrorException {

    }

    @Override
    public void resetWallet(byte[] pin, byte[] signature) throws SecurityErrorException {

    }


}
