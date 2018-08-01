package lr.com.wallet.utils;

import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lr.com.wallet.pojo.TxBean;

/**
 * Created by DT0814 on 2018/7/30.
 */

public class UnfinishedTxPool {
    private static String poolName = "txPool";

    public static void addUnfinishedTx(TxBean tx, String coinId) {
        SharedPreferencesUtils.writeString(poolName, tx.getHash() + "_" + coinId, JsonUtils.objectToJson(tx));
    }

    public static void deleteUnfinishedTx(TxBean tx, String coinId) {
        SharedPreferencesUtils.deleteString(poolName, tx.getHash() + "_" + coinId);
    }

    public static List<TxBean> getUnfinishedTxByCoinid(String coinId) {
        List<TxBean> result = new ArrayList<>();
        Map<String, Object> all = SharedPreferencesUtils.getAll(poolName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            all.forEach((k, v) -> {
                if (k.endsWith("_" + coinId)) {
                    String json = v.toString();
                    TxBean txBean = JsonUtils.jsonToPojo(json, TxBean.class);
                    if (null != txBean) {
                        result.add(txBean);
                    }
                }
            });
        }
        return result;
    }
}
