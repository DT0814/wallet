package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/7/31.
 */

public class TxStatusBean {
    private String status;
    private String message;
    private TxStatusResult result;

    @Override
    public String toString() {
        return "TxStatusBean{" +
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

    public TxStatusResult getResult() {
        return result;
    }

    public void setResult(TxStatusResult result) {
        this.result = result;
    }


}

