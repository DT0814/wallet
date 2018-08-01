package lr.com.wallet.pojo;

import java.util.List;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TxPojo {
    private String status;
    private String message;
    private List<TxBean> result;

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

    public List<TxBean> getResult() {
        return result;
    }

    public void setResult(List<TxBean> result) {
        this.result = result;
    }

    public TxPojo() {
    }

    @Override
    public String toString() {
        return "TxPojo{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
