package lr.com.wallet.utils;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class URLUtils {
    static String txApiUrl = "https://etherscan.io/tx/";

    public static String getTxUrl(String hash) {
        return txApiUrl + hash;
    }
}
