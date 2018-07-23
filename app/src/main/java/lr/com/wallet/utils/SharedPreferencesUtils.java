package lr.com.wallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dt0814 on 2018/7/16.
 */

public class SharedPreferencesUtils {
    private static Context context;

    public static void init(Context context) {
        SharedPreferencesUtils.context = context;
    }

    /**
     * 写入一个字符串
     *
     * @param sfName
     * @param key
     * @param value
     */
    public static void writeString(String sfName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sfName, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getString(String sfName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sfName, MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static Map<String, Object> getAll(String sfName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sfName, MODE_PRIVATE);
        Map<String, Object> all = (Map<String, Object>) sharedPreferences.getAll();
        return all;
    }

    public static Long getLong(String sfName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sfName, MODE_PRIVATE);
        return sharedPreferences.getLong(key, 1L);
    }

    public static void writeLong(String sfName, String key, Long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sfName, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.commit();
    }

}
