package com.hunter.wallet.service;

class NativeSecurityService {
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

    native byte[] getWalletList();

    native byte[] createWallet(byte[] name, byte[] password);

    native byte[] deleteWallet(int id, byte[] password);

    native byte[] recoverWalletByMnemonic(byte[] name, byte[] password, byte[] mnemonic);

    native byte[] recoverWalletByKeystore(byte[] name, byte[] password, byte[] keystore);

    native byte[] recoverWalletByPrikey(byte[] name, byte[] password, byte[] prikey);

    native byte[] signature(int id, byte[] password, byte[] data);

    native byte[] getKeystore(int id, byte[] password);

    native byte[] getMnemonic(int id, byte[] password);

    native byte[] getPrikey(int id, byte[] password);

    native byte[] getPubkey(int id);

    native byte[] changeName(int id, byte[] newName);

    native byte[] changePassword(int id, byte[] oldPassword, byte[] newPassword);
}
