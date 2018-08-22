package lr.com.wallet.utils;

import android.util.Log;

import com.hunter.wallet.service.EthWallet;

import lr.com.wallet.pojo.ETHWallet;

/**
 * Created by DT0814 on 2018/8/21.
 */

public class ConvertPojo {
    public static ETHWallet toETHWallet(EthWallet wallet) {
        ETHWallet ethWallet = new ETHWallet();
        ethWallet.setBalance("0");
        ethWallet.setAddress(wallet.getAddr());
        ethWallet.setId(Long.parseLong(wallet.getId() + ""));
        ethWallet.setName(wallet.getName());
        Log.i("转换完成的钱包", ethWallet.toString());
        return ethWallet;
    }
}
