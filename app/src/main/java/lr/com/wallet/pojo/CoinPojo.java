package lr.com.wallet.pojo;

/**
 * Created by dt0814 on 2018/7/21.
 */

public class CoinPojo {
    private Long coinId;
    private String coinSymbolName;
    private String coinCount;
    private String coinAddress;
    private Long walletId;
    private String coinName;

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public Long getCoinId() {
        return coinId;
    }

    public void setCoinId(Long coinId) {
        this.coinId = coinId;
    }

    public String getCoinSymbolName() {
        return coinSymbolName;
    }

    public void setCoinSymbolName(String coinSymbolName) {
        this.coinSymbolName = coinSymbolName;
    }

    public String getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(String coinCount) {
        this.coinCount = coinCount;
    }

    public String getCoinAddress() {
        return coinAddress;
    }

    public void setCoinAddress(String coinAddress) {
        this.coinAddress = coinAddress;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    @Override
    public String toString() {
        return "CoinPojo{" +
                "coinId=" + coinId +
                ", coinSymbolName='" + coinSymbolName + '\'' +
                ", coinCount='" + coinCount + '\'' +
                ", coinAddress='" + coinAddress + '\'' +
                ", walletId=" + walletId +
                '}';
    }
}
