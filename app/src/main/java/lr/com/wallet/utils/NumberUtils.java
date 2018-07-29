package lr.com.wallet.utils;

/**
 * Created by DT0814 on 2018/7/29.
 */

public class NumberUtils {
    public static Long trnNumber(String value) {
        int i;
        for (i = 2; i < value.length(); i++) {
            if (value.charAt(i) != '0') {
                break;
            }
        }
        value = value.substring(i, value.length());
        if (value.equals("")) {
        }
        long l = Long.parseLong(value, 16);
        return l;
    }
}