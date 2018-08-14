package lr.com.wallet.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import lr.com.wallet.pojo.ETHPriceResult;
import lr.com.wallet.pojo.TxPojo;

/**
 * Created by DT0814 on 2018/8/4.
 */

public class HTTPUtils {
    public static <T> T getUtils(String urlStr, Class<T> resultType) {
        URL url = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                if ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                T t = JsonUtils.jsonToPojo(sb.toString(), resultType);
                return t;
            } else {
                Log.e("getUtils", "查询失败");
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T ETHPriceResult(String urlStr, Class<T> resultType) {
        URL url = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                if ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                T t = JsonUtils.jsonToPojo(sb.toString().substring(1, sb.length() - 1), resultType);
                return t;
            } else {
                Log.e("getUtils", "查询失败");
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
