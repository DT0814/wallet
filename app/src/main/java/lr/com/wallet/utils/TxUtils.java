package lr.com.wallet.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lr.com.wallet.pojo.TxPojo;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TxUtils {
    private static String urlStr = "http://api.etherscan.io/api";
    private static String apiKey = "c0oGHqQQlq6XJU2kz5DL";

    public static TxPojo getTransactionPojoByAddress(String address) throws Exception {
        HttpUrl httpUrl = new HttpUrl("account", address, "txlist", "desc",
                apiKey, "");
        TxPojo txPojo = HTTPUtils.getUtils(httpUrl.toString(), TxPojo.class);
        return txPojo;
    }


    public static TxPojo getTransactionPojoByAddressAndContractAddress(String address, String contractAddress) throws Exception {
        HttpUrl httpUrl = new HttpUrl("account", address, "tokentx", "desc",
                apiKey, contractAddress);
        TxPojo txPojo = HTTPUtils.getUtils(httpUrl.toString(), TxPojo.class);
        return txPojo;
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
