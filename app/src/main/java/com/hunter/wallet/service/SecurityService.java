package com.hunter.wallet.service;

import java.util.List;

public interface SecurityService {

    List<WalletInfo> getAllWallet() throws SecurityErrorException;

    WalletInfo createWallet(String name, String password) throws SecurityErrorException;

    void deleteWallet(int id, String password) throws SecurityErrorException;

    WalletInfo recoverByMnemonic(String name, String password, String mnemonic, String path) throws SecurityErrorException;

    WalletInfo recoverByKeystore(String name, String password, String keystore) throws SecurityErrorException;

    WalletInfo recoverByPrikey(String name, String password, byte[] prikey) throws SecurityErrorException;

    byte[] signature(int id, String password, byte[] data) throws SecurityErrorException;

    String getKeystore(int id, String password) throws SecurityErrorException;

    String getMnemonic(int id, String password) throws SecurityErrorException;

    byte[] getPrikey(int id, String password) throws SecurityErrorException;

    byte[] getPubkey(int id) throws SecurityErrorException;

    void changeName(int id, String newName) throws SecurityErrorException;

    void changePassword(int id, String password, String newPassword) throws SecurityErrorException;
}
