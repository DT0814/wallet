package lr.com.wallet.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import lr.com.wallet.pojo.TransactionBean;
import lr.com.wallet.pojo.TransactionPojo;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TransactionUtils {
    public static TransactionPojo getTransactionPojo(String address) throws Exception {
        URL url = new URL("http://api.etherscan.io/api?module=account&action=txlist&" +
                "address=" + address + "&sort=desc&apikey=YourApiKeyToken");
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
            TransactionPojo transactionPojo = JsonUtils.jsonToPojo(sb.toString(), TransactionPojo.class);
            return transactionPojo;
        } else {
            System.out.println("error");
            return null;
        }

    }
}
