package lr.com.wallet.pojo;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by DT0814 on 2018/7/30.
 */

public class TxCacheBean {
    private int num;
    private Long coinId;
    private Long walletId;
    private List<TxBean> data;
    private List<TxBean> errData = new ArrayList<>();

    public TxCacheBean() {
    }

    public TxCacheBean(Long coinId, Long walletId, List<TxBean> data) {
        this.num = data.size();
        this.coinId = coinId;
        this.walletId = walletId;
        this.data = data;
    }

    @Override
    public String toString() {
        return "TxCacheBean{" +
                "num=" + num +
                ", coinId=" + coinId +
                ", walletId=" + walletId +
                ", data=" + data +
                ", errData=" + errData +
                '}';
    }

    public List<TxBean> getErrData() {
        return errData;
    }

    public void setErrData(List<TxBean> errData) {
        this.errData = errData;
    }


    public Long getCoinId() {
        return coinId;
    }

    public void setCoinId(Long coinId) {
        this.coinId = coinId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<TxBean> getData() {
        return data;
    }

    public void setData(List<TxBean> data) {
        this.data = data;
    }
}
