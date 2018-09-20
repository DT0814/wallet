package lr.com.wallet.dao;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by dt0814 on 2018/7/18.
 */
public class CacheWalletDao {

    /**
     * 将当前钱包持久化
     *
     * @param ethCacheWallet
     */
    public static void writeCurrentJsonWallet(ETHCacheWallet ethCacheWallet) {
        SharedPreferencesUtils.writeString("wallet", "wallet", JsonUtils.objectToJson(ethCacheWallet));
    }

    /**
     * 清空当前持久钱包
     */
    public static void deleteCurrentJsonWallet() {
        SharedPreferencesUtils.deleteString("wallet", "wallet");
    }

    /**
     * 获得当前使用钱包
     *
     * @return
     */
    public static ETHCacheWallet getCurrentWallet() {
        String json = SharedPreferencesUtils.getString("wallet", "wallet");
        return JsonUtils.jsonToPojo(json, ETHCacheWallet.class);
    }

    /**
     * 持久化一个钱包
     *
     * @param ethCacheWallet
     */
    public static void writeJsonWallet(ETHCacheWallet ethCacheWallet) {
        SharedPreferencesUtils.writeString("wallet", "wallet_" + ethCacheWallet.getId(), JsonUtils.objectToJson(ethCacheWallet));
    }

    /**
     * 根据钱包id得到一个钱包
     *
     * @param ethid
     */
    public static ETHCacheWallet getWalletByWalletId(Long ethid) {
        String walletJson = SharedPreferencesUtils.getString("wallet", "wallet_" + ethid);
        ETHCacheWallet ethCacheWallet = JsonUtils.jsonToPojo(walletJson, ETHCacheWallet.class);
        return ethCacheWallet;
    }

    /**
     * 获取所有钱包
     */

    public static List<ETHCacheWallet> getAllWallet() {
        Set<ETHCacheWallet> set = new HashSet<>();
        Map<String, Object> walletJsons = SharedPreferencesUtils.getAll("wallet");
        walletJsons.forEach((k, v) -> {
            set.add(JsonUtils.jsonToPojo(v.toString(), ETHCacheWallet.class));
        });
        List<ETHCacheWallet> list = new ArrayList<>(set);
        list.sort(new Comparator<ETHCacheWallet>() {
            @Override
            public int compare(ETHCacheWallet ethCacheWallet, ETHCacheWallet t1) {
                return ethCacheWallet.getId().intValue() - t1.getId().intValue();
            }
        });
        return list;
    }

    public static boolean CheckContains(ETHCacheWallet e) {
        List<ETHCacheWallet> allWallet = getAllWallet();
        return allWallet.contains(e);
    }

    /**
     * @param ethCacheWallet
     * @return 2删除了一个非当前钱包1删除了一个当前钱包需要更新0删除后没有钱包了
     */
    public static int deleteWallet(ETHCacheWallet ethCacheWallet) {
        CoinDao.deleteByWalletId(ethCacheWallet.getId());
        ETHCacheWallet currentWallet = getCurrentWallet();
        if (null != currentWallet && currentWallet.equals(ethCacheWallet)) {
            SharedPreferencesUtils.deleteString("wallet", "wallet_" + ethCacheWallet.getId());
            List<ETHCacheWallet> allWallet = getAllWallet();
            if (allWallet.size() <= 1) {
                //如果删除后没钱包了就把当前钱包清空
                SharedPreferencesUtils.deleteString("wallet", "wallet");
                return 0;
            } else {
                //获取剩余钱包的一个钱包
                for (ETHCacheWallet wallet : allWallet) {
                    if (!wallet.equals(currentWallet)) {
                        writeCurrentJsonWallet(wallet);
                        break;
                    }
                }
                return 1;
            }
        } else {
            SharedPreferencesUtils.deleteString("wallet", "wallet_" + ethCacheWallet.getId());
            return 2;
        }
    }

    public static void update(ETHCacheWallet ethCacheWallet) {
        if (ethCacheWallet.getId().intValue() == getCurrentWallet().getId().intValue()) {
            writeCurrentJsonWallet(ethCacheWallet);
        }
        SharedPreferencesUtils.writeString("wallet", "wallet_" + ethCacheWallet.getId(), JsonUtils.objectToJson(ethCacheWallet));
    }

    public static void deleteCache(ETHCacheWallet ethCacheWallet) {
        CoinDao.deleteCoinCache(ethCacheWallet);
    }

    public static boolean haveWallet() {
        return null != getCurrentWallet();
    }

    public static void clean() {
        for (ETHCacheWallet ethCacheWallet : getAllWallet()) {
            deleteWallet(ethCacheWallet);
            deleteCache(ethCacheWallet);
        }
        deleteCurrentJsonWallet();

    }
}
