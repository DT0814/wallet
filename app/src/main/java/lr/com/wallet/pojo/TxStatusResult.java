package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/7/31.
 */

public class TxStatusResult {
    private String status;

    @Override
    public String toString() {
        return "Result{" +
                "status='" + status + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
