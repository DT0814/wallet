package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/8/4.
 */

public class ExchangeRate {
    private String status;
    private String msg;
    private ExchangeRateResult result;

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ExchangeRateResult getResult() {
        return result;
    }

    public void setResult(ExchangeRateResult result) {
        this.result = result;
    }
}
