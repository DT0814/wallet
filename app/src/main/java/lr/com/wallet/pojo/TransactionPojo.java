package lr.com.wallet.pojo;

import java.util.List;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TransactionPojo {
    private String status;
    private String message;
    private List<TransactionBean> result;

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

    public List<TransactionBean> getResult() {
        return result;
    }

    public void setResult(List<TransactionBean> result) {
        this.result = result;
    }

    public TransactionPojo() {
    }

    @Override
    public String toString() {
        return "TransactionPojo{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
