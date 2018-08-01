package lr.com.wallet.utils;

import java.util.Comparator;

import lr.com.wallet.pojo.TxBean;

/**
 * Created by DT0814 on 2018/7/30.
 */

public class TxComparator implements Comparator<TxBean> {
    @Override
    public int compare(TxBean txBean, TxBean t1) {

        int a = Integer.parseInt(txBean.getTimeStamp());
        int b = Integer.parseInt(t1.getTimeStamp());
        return b - a;
    }
}
