package lr.com.wallet.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lr.com.wallet.pojo.TxStatusBean;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TxStatusUtils {
    private static String urlStr = "http://api.etherscan.io/api";
    private static String apiKey = "c0oGHqQQlq6XJU2kz5DL";


    public static TxStatusBean getTxStatusByHash(String hash) throws Exception {
        HttpUrl httpUrl = new HttpUrl("transaction", hash, "gettxreceiptstatus",
                apiKey);
        URL url = new URL(httpUrl.toString());
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
            TxStatusBean txStatusBean = JsonUtils.jsonToPojo(sb.toString(), TxStatusBean.class);
            return txStatusBean;
        } else {
            System.out.println("error");
            return null;
        }
    }

    private static class HttpUrl {
        private String module;
        private String txhash;
        private String action;
        private String apikey;

        @Override
        public String toString() {
            return urlStr + "?" +
                    "module=" + module +
                    "&txhash=" + txhash +
                    "&action=" + action +
                    "&apikey=" + apikey;
        }

        public HttpUrl(String module, String txhash, String action, String apikey) {
            this.module = module;
            this.txhash = txhash;
            this.action = action;
            this.apikey = apikey;
        }

    }
}
