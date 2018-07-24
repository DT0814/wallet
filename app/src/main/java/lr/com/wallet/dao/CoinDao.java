package lr.com.wallet.dao;

import android.os.Build;

import org.bitcoinj.core.Coin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.CoinUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;
import lr.com.wallet.utils.Web3jUtil;

/**
 * Created by dt0814 on 2018/7/21.
 */

public class CoinDao {
    private static String sfName = "coin";
    private static String coinId = "coin";

    public static CoinPojo writeETHConinPojo() {

        CoinPojo coin = new CoinPojo();
        ETHWallet currentWallet = WalletDao.getCurrentWallet();
        coin.setCoinAddress("eth");
        coin.setWalletId(currentWallet.getId());
        coin.setCoinSymbolName("ETH");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    coin.setCoinCount(Web3jUtil.ethGetBalance(currentWallet.getAddress()));
                    SharedPreferencesUtils.writeString(sfName,
                            "coin_" + coin.getCoinId() + "_" + coin.getWalletId(),
                            JsonUtils.objectToJson(coin));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        coin.setCoinId(0L);
        SharedPreferencesUtils.writeString(sfName,
                "coin_" + coin.getCoinId() + "_" + coin.getWalletId(),
                JsonUtils.objectToJson(coin));
        return coin;
    }


    public static CoinPojo addConinPojo(String coinAddress) {
        String symbolName = CoinUtils.getSymbolName(coinAddress);
        String coinName = CoinUtils.getName(coinAddress);
        if (null == symbolName || symbolName.trim().equals("")) {
            return null;
        } else {
            CoinPojo coin = new CoinPojo();
            ETHWallet currentWallet = WalletDao.getCurrentWallet();
            coin.setCoinAddress(coinAddress);
            coin.setWalletId(currentWallet.getId());
            coin.setCoinSymbolName(symbolName);
            coin.setCoinName(coinName);
            coin.setCoinCount(CoinUtils.getBalanceOf(coinAddress, currentWallet.getAddress()));
            coin.setCoinId(SharedPreferencesUtils.getLong(sfName, coinId));
            SharedPreferencesUtils.writeLong(sfName, coinId, coin.getCoinId() + 1);
            SharedPreferencesUtils.writeString(sfName,
                    "coin_" + coin.getCoinId() + "_" + coin.getWalletId(),
                    JsonUtils.objectToJson(coin));
            return coin;
        }
    }

    public static CoinPojo addConinPojo(CoinPojo coin) {
        ETHWallet currentWallet = WalletDao.getCurrentWallet();
        coin.setCoinCount("0");
        coin.setCoinId(SharedPreferencesUtils.getLong(sfName, coinId));
        SharedPreferencesUtils.writeLong(sfName, coinId, coin.getCoinId() + 1);
        SharedPreferencesUtils.writeString(sfName,
                "coin_" + coin.getCoinId() + "_" + coin.getWalletId(),
                JsonUtils.objectToJson(coin));
        return coin;
    }


    public static CoinPojo getConinByCoinId(Long coinId) {
        ETHWallet currentWallet = WalletDao.getCurrentWallet();
        String string = SharedPreferencesUtils.getString(sfName, "coin_" + coinId + "_" + currentWallet.getId());
        return JsonUtils.jsonToPojo(string, CoinPojo.class);
    }

    public static List<CoinPojo> getConinListByWalletId(Long walletId) {
        List<CoinPojo> result = new ArrayList<>();
        Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            all.forEach((k, v) -> {
                if (k.endsWith("_" + walletId.toString())) {
                    result.add(JsonUtils.jsonToPojo(v.toString(), CoinPojo.class));
                }
            });
        }
        return result;
    }
}
