package lr.com.wallet.dao;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by dt0814 on 2018/7/18.
 */
@SuppressLint("NewApi")
public class WalletDao {

    /**
     * 将当前钱包持久化
     *
     * @param ethWallet
     */
    public static void writeCurrentJsonWallet(ETHWallet ethWallet) {
        SharedPreferencesUtils.writeString("wallet", "wallet", JsonUtils.objectToJson(ethWallet));
    }

    /**
     * 获得当前使用钱包
     *
     * @return
     */
    public static ETHWallet getCurrentWallet() {
        String json = SharedPreferencesUtils.getString("wallet", "wallet");
        return JsonUtils.jsonToPojo(json, ETHWallet.class);
    }

    /**
     * 获取一个钱包可用id
     *
     * @return
     */
    public static Long getNewWalletId() {
        Long aLong = SharedPreferencesUtils.getLong("Info", "walletId");
        SharedPreferencesUtils.writeLong("Info", "walletId", aLong + 1);
        return aLong;
    }

    /**
     * 持久化一个钱包
     *
     * @param ethWallet
     */
    public static void writeJsonWallet(ETHWallet ethWallet) {
        SharedPreferencesUtils.writeString("wallet", "wallet_" + ethWallet.getId(), JsonUtils.objectToJson(ethWallet));
    }

    /**
     * 根据钱包id得到一个钱包
     *
     * @param ethid
     */
    public static ETHWallet getWalletByWalletId(Long ethid) {
        String walletJson = SharedPreferencesUtils.getString("wallet", "wallet_" + ethid);
        ETHWallet ethWallet = JsonUtils.jsonToPojo(walletJson, ETHWallet.class);
        return ethWallet;
    }

    /**
     * 获取所有钱包
     */

    public static List<ETHWallet> getAllWallet() {
        Set<ETHWallet> set = new HashSet<>();
        Map<String, Object> walletJsons = SharedPreferencesUtils.getAll("wallet");
        walletJsons.forEach((k, v) -> {
            set.add(JsonUtils.jsonToPojo(v.toString(), ETHWallet.class));
        });
        List<ETHWallet> list = new ArrayList<>(set);
        list.sort(new Comparator<ETHWallet>() {
            @Override
            public int compare(ETHWallet ethWallet, ETHWallet t1) {
                return ethWallet.getId().intValue() - t1.getId().intValue();
            }
        });
        return list;
    }

    public static boolean CheckContains(ETHWallet e) {
        List<ETHWallet> allWallet = getAllWallet();
        return allWallet.contains(e);
    }

    /**
     * @param ethWallet
     * @return 2删除了一个非当前钱包1删除了一个当前钱包需要更新0删除后没有钱包了
     */
    public static int deleteWallet(ETHWallet ethWallet) {
        CoinDao.deleteByWalletId(ethWallet.getId());
        ETHWallet currentWallet = getCurrentWallet();
        if (currentWallet.equals(ethWallet)) {
            SharedPreferencesUtils.deleteString("wallet", "wallet_" + ethWallet.getId());
            List<ETHWallet> allWallet = getAllWallet();
            if (allWallet.size() <= 1) {
                //如果删除后没钱包了就把当前钱包清空
                SharedPreferencesUtils.deleteString("wallet", "wallet");
                return 0;
            } else {
                //获取剩余钱包的一个钱包
                for (ETHWallet wallet : allWallet) {
                    if (!wallet.equals(currentWallet)) {
                        writeCurrentJsonWallet(wallet);
                        break;
                    }
                }
                return 1;
            }
        } else {
            SharedPreferencesUtils.deleteString("wallet", "wallet_" + ethWallet.getId());
            return 2;
        }
    }

    public static void update(ETHWallet ethWallet) {
        if (ethWallet.getId().intValue() == getCurrentWallet().getId().intValue()) {
            writeCurrentJsonWallet(ethWallet);
        }
        SharedPreferencesUtils.writeString("wallet", "wallet_" + ethWallet.getId(), JsonUtils.objectToJson(ethWallet));
    }
}
