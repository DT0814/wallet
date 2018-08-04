package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/8/4.
 */

public class ETHPrice {
    private String status;
    private String message;
    private ETHPriceResult result;

    @Override
    public String toString() {
        return "ETHPrice{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ETHPriceResult getResult() {
        return result;
    }

    public void setResult(ETHPriceResult result) {
        this.result = result;
    }
}
