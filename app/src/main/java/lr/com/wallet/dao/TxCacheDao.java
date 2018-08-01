package lr.com.wallet.dao;

import lr.com.wallet.pojo.TxCacheBean;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by DT0814 on 2018/7/31.
 */

public class TxCacheDao {
    private static String sfname = "txCache";

    public static void addTxCache(TxCacheBean cache) {
        SharedPreferencesUtils.writeString(sfname, cache.getWalletId() + "_" + cache.getCoinId(), JsonUtils.objectToJson(cache));
    }


    public static TxCacheBean getTxCache(String walletId, String coinId) {
        String string = SharedPreferencesUtils.getString(sfname, walletId + "_" + coinId);
        if (null == string || string.trim().equals("")) {
            return null;
        }
        return JsonUtils.jsonToPojo(string, TxCacheBean.class);
    }

    public static void delete(String walletId, String coinId) {
        SharedPreferencesUtils.deleteString(sfname, walletId + "_" + coinId);
    }
}
