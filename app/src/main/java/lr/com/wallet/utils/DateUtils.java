package lr.com.wallet.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class DateUtils {
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getDateFormatByString(Long l) {
    return sf.format(new Date(l));
    }
}
