package lr.com.wallet.utils;

import android.content.SharedPreferences;

import lr.com.wallet.pojo.WalletPojo;

/**
 * Created by dt0814 on 2018/7/12.
 */

public class WalletPojoUtil {
    public static boolean write(SharedPreferences sf, WalletPojo walletPojo) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putString("address", walletPojo.getAddress());
        edit.putString("pubKey", walletPojo.getPubKey());
        edit.putString("PrvKey", walletPojo.getPrvKey());
        edit.putString("words", walletPojo.getWords());
        boolean b = edit.commit();
        return b;
    }

    public static WalletPojo read(SharedPreferences sf) {
        WalletPojo walletPojo = new WalletPojo();
        walletPojo.setAddress(sf.getString("address", ""));
        walletPojo.setPubKey(sf.getString("pubKey", ""));
        walletPojo.setPrvKey(sf.getString("PrvKey", ""));
        walletPojo.setWords(sf.getString("words", ""));
        return walletPojo;
    }
}
