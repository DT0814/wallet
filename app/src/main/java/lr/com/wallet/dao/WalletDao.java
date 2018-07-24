package lr.com.wallet.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dt0814 on 2018/7/18.
 */

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
     * 根据钱包id得到一个钱包
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<ETHWallet> getAllWallet() {
        Set<ETHWallet> set = new HashSet<>();
        Map<String, Object> walletJsons = SharedPreferencesUtils.getAll("wallet");
        walletJsons.forEach((k, v) -> {
            set.add(JsonUtils.jsonToPojo(v.toString(), ETHWallet.class));
        });
        List<ETHWallet> list = new ArrayList<>(set);
        return list;
    }
}
