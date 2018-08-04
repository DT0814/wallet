package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/8/4.
 */

public class ETHPriceResult {
    private String ethbtc;
    private String ethbtc_timestamp;
    private String ethusd;
    private String ethusd_timestamp;

    @Override
    public String toString() {
        return "ETHPriceResult{" +
                "ethbtc='" + ethbtc + '\'' +
                ", ethbtc_timestamp='" + ethbtc_timestamp + '\'' +
                ", ethusd='" + ethusd + '\'' +
                ", ethusd_timestamp='" + ethusd_timestamp + '\'' +
                '}';
    }

    public String getEthbtc() {
        return ethbtc;
    }

    public void setEthbtc(String ethbtc) {
        this.ethbtc = ethbtc;
    }

    public String getEthbtc_timestamp() {
        return ethbtc_timestamp;
    }

    public void setEthbtc_timestamp(String ethbtc_timestamp) {
        this.ethbtc_timestamp = ethbtc_timestamp;
    }

    public String getEthusd() {
        return ethusd;
    }

    public void setEthusd(String ethusd) {
        this.ethusd = ethusd;
    }

    public String getEthusd_timestamp() {
        return ethusd_timestamp;
    }

    public void setEthusd_timestamp(String ethusd_timestamp) {
        this.ethusd_timestamp = ethusd_timestamp;
    }
}
