package lr.com.wallet.dao;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lr.com.wallet.R;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.CoinUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;
import lr.com.wallet.utils.Web3jUtil;

/**
 * Created by dt0814 on 2018/7/21.
 */
@SuppressLint("NewApi")
public class CoinDao {
    private static String sfName = "coin";
    private static String coinId = "coin";

    public static CoinPojo writeETHConinPojo() {

        CoinPojo coin = new CoinPojo();
        coin.setCoinCount("0");
        ETHWallet currentWallet = WalletDao.getCurrentWallet();
        coin.setCoinAddress("0x000000000000000000000000000000");
        coin.setWalletId(currentWallet.getId());
        coin.setCoinSymbolName("ETH");
        coin.setCoinId(SharedPreferencesUtils.getLong(sfName, coinId));
        SharedPreferencesUtils.writeLong(sfName, coinId, coin.getCoinId() + 1);
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


        SharedPreferencesUtils.writeString(sfName,
                "coin_" + coin.getCoinId() + "_" + coin.getWalletId(),
                JsonUtils.objectToJson(coin));
        return coin;
    }

    private static boolean contain(String coinAddress) {
        String result = SharedPreferencesUtils.getString("ethWallet", "coinList");
        List<CoinPojo> coinPojos = JsonUtils.jsonToList(result, CoinPojo.class);
        for (CoinPojo coinPojo : coinPojos) {
            if (coinPojo.getCoinAddress().equals(coinAddress)) {
                return true;
            }
        }
        return false;
    }

    public static CoinPojo addCoinPojo(String coinAddress, String fromAddr) {

        //之前未添加过同地址代币
        if (contain(coinAddress)) {
            return null;
        }
        String symbolName = CoinUtils.getSymbolName(coinAddress, fromAddr);
        if (null == symbolName || symbolName.trim().equals("")) {
            return null;
        }
        String coinName = CoinUtils.getName(coinAddress, fromAddr);
        if (null == coinName || coinName.trim().equals("")) {
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

    public static CoinPojo addCoinPojo(CoinPojo coin) {
        ETHWallet currentWallet = WalletDao.getCurrentWallet();
        coin.setCoinCount("0");
        coin.setWalletId(currentWallet.getId());
        coin.setCoinId(SharedPreferencesUtils.getLong(sfName, coinId));
        SharedPreferencesUtils.writeLong(sfName, coinId, coin.getCoinId() + 1);
        SharedPreferencesUtils.writeString(sfName,
                "coin_" + coin.getCoinId() + "_" + currentWallet.getId(),
                JsonUtils.objectToJson(coin));
        return coin;
    }

    public static CoinPojo deleteConinPojo(CoinPojo coin) {
        ETHWallet currentWallet = WalletDao.getCurrentWallet();
        Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
        all.forEach((k, v) -> {
            CoinPojo coinPojo = JsonUtils.jsonToPojo(v.toString(), CoinPojo.class);
            if (null != coinPojo
                    && coinPojo.getCoinAddress().equalsIgnoreCase(coin.getCoinAddress())
                    && coinPojo.getWalletId().equals(currentWallet.getId())) {
                SharedPreferencesUtils.deleteString(sfName,
                        "coin_" + coinPojo.getCoinId() + "_" + currentWallet.getId());
            }
        });
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

    public static CoinPojo updateCoinPojo(CoinPojo coin) {
        SharedPreferencesUtils.writeString(sfName,
                "coin_" + coin.getCoinId() + "_" + coin.getWalletId(),
                JsonUtils.objectToJson(coin));
        return coin;
    }

    public static boolean CheckContains(String address, Long walletId) {
        List<CoinPojo> coninListByWalletId = getConinListByWalletId(walletId);
        CoinPojo coinPojo = new CoinPojo();
        coinPojo.setCoinAddress(address);
        return coninListByWalletId.contains(coinPojo);
    }

    public static void deleteByWalletId(Long id) {
        Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
        all.forEach((k, v) -> {
            CoinPojo coinPojo = JsonUtils.jsonToPojo(v.toString(), CoinPojo.class);
            if (null != coinPojo && coinPojo.getWalletId().equals(id)) {
                deleteConinPojo(coinPojo);
            }

        });
    }
}
