package lr.com.wallet.pojo;

/**
 * Created by dt0814 on 2018/7/12.
 */

public class WalletPojo {
    private String address;
    private String pubKey;
    private String prvKey;
    private String words;

    public WalletPojo(String address, String pubKey, String prvKey, String words) {
        this.address = address;
        this.pubKey = pubKey;
        this.prvKey = prvKey;
        this.words = words;
    }

    public WalletPojo() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrvKey() {
        return prvKey;
    }

    public void setPrvKey(String prvKey) {
        this.prvKey = prvKey;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "WalletPojo{" +
                "address='" + address + '\'' +
                ", pubKey='" + pubKey + '\'' +
                ", prvKey='" + prvKey + '\'' +
                ", words='" + words + '\'' +
                '}';
    }
}
