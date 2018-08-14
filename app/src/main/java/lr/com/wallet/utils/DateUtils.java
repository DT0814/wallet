package lr.com.wallet.utils;

import com.github.mikephil.charting.data.CandleData;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class DateUtils {

    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getDateFormatByString(Long l) {
        return sf.format(new Date(l));
    }

    //获取代币详细信息页面折线图X轴数据
    public static String[] getLineDataXData(int len) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int currMonth = calendar.get(Calendar.MONTH);
        String[] result = new String[len];
        for (; len > 0; len--) {
            result[len - 1] = (currMonth + 1) + "/" + day;
            day = day - 2;
            if (day <= 0) {
                calendar.set(calendar.get(Calendar.YEAR), currMonth, 0);
                day = day + calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                currMonth = calendar.get(Calendar.MONTH);
            }
        }
        return result;
    }
}
