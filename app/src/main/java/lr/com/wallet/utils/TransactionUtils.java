package lr.com.wallet.utils;

import org.junit.Test;

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
    private static String urlStr = "http://api.etherscan.io/api";
    private static String apiKey = "c0oGHqQQlq6XJU2kz5DL";

    public static TransactionPojo getTransactionPojoByAddress(String address) throws Exception {
        HttpUrl httpUrl = new HttpUrl("account", address, "txlist", "desc",
                apiKey, "");
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
            TransactionPojo transactionPojo = JsonUtils.jsonToPojo(sb.toString(), TransactionPojo.class);
            return transactionPojo;
        } else {
            System.out.println("error");
            return null;
        }

    }

    @Test
    public void test() {
        try {
            TransactionPojo transactionPojoByAddress = getTransactionPojoByAddress(
                    "0x91164cba5f62df3f0f20606ab0124b13c2e2029e");
            TransactionPojo tx2 = getTransactionPojoByAddressAndContractAddress(
                    "0x91164cba5f62df3f0f20606ab0124b13c2e2029e",
                    "0xb364dc7d097612face58b4860e982a7a99f4f392");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TransactionPojo getTransactionPojoByAddressAndContractAddress(String address, String contractAddress) throws Exception {
        HttpUrl httpUrl = new HttpUrl("account", address, "tokentx", "desc",
                apiKey, contractAddress);
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
            TransactionPojo transactionPojo = JsonUtils.jsonToPojo(sb.toString(), TransactionPojo.class);
            return transactionPojo;
        } else {
            System.out.println("error");
            return null;
        }

    }

    private static class HttpUrl {
        private String module;
        private String address;
        private String action;
        private String sort;
        private String apikey;
        private String contractAddress;

        public HttpUrl(String module, String address, String action, String sort, String apikey, String contractAddress) {
            this.module = module;
            this.address = address;
            this.action = action;
            this.sort = sort;
            this.apikey = apikey;
            this.contractAddress = contractAddress;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getApikey() {
            return apikey;
        }

        public void setApikey(String apikey) {
            this.apikey = apikey;
        }

        public String getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
        }

        @Override
        public String toString() {
            return urlStr + "?" +
                    "module=" + module +
                    "&address=" + address +
                    "&action=" + action +
                    "&sort=" + sort +
                    "&apikey=" + apikey +
                    "&contractAddress=" + contractAddress;
        }
    }
}
