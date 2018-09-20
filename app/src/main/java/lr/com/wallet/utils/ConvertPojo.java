package lr.com.wallet.utils;

import android.util.Log;

import com.hunter.wallet.service.WalletInfo;

import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.pojo.ETHWallet;

/**
 * Created by DT0814 on 2018/8/21.
 */

public class ConvertPojo {
    public static ETHCacheWallet toETHCacheWallet(WalletInfo wallet) {
        ETHCacheWallet ethCacheWallet = new ETHCacheWallet();
        ethCacheWallet.setBalance("0");
        ethCacheWallet.setAddress(wallet.getAddr());
        ethCacheWallet.setId(Long.parseLong(wallet.getId() + ""));
        ethCacheWallet.setName(wallet.getName());
        return ethCacheWallet;
    }

    public static ETHWallet toETHWallet(WalletInfo wallet) {
        ETHWallet ethWallet = new ETHWallet();
        return ethWallet;
    }

    public static WalletInfo toWalletInfo(ETHWallet wallet) {
        WalletInfo walletInfo = new WalletInfo();
        walletInfo.setId(wallet.getId());
        walletInfo.setPubkey(wallet.getPubKey());
        walletInfo.setName(wallet.getName());
        return walletInfo;
    }
}
