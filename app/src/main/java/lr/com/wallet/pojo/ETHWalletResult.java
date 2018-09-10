package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/9/10.
 */

public class ETHWalletResult {
    private Object data;
    private boolean error;
    private int code;

    public static ETHWalletResult err(int code) {
        return new ETHWalletResult(code);
    }

    public static ETHWalletResult instance(Object data) {
        return new ETHWalletResult(data);
    }

    private ETHWalletResult(int code) {
        this.error = true;
        this.code = code;
    }

    private ETHWalletResult(Object data) {
        this.error = false;
        this.data = data;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
