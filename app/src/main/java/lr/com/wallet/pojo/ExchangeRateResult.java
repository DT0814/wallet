package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/8/4.
 */

public class ExchangeRateResult {
    private String from;
    private String to;
    private String fromname;
    private String toname;
    private String updatetime;
    private String rate;
    private String camount;

    @Override
    public String toString() {
        return "ExchangeRateResult{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", fromname='" + fromname + '\'' +
                ", toname='" + toname + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", rate='" + rate + '\'' +
                ", camount='" + camount + '\'' +
                '}';
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public String getToname() {
        return toname;
    }

    public void setToname(String toname) {
        this.toname = toname;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCamount() {
        return camount;
    }

    public void setCamount(String camount) {
        this.camount = camount;
    }
}
