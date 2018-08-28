package lr.com.wallet.test;

import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import lr.com.wallet.pojo.Price;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.pojo.TxPojo;
import lr.com.wallet.utils.AddressEncoder;
import lr.com.wallet.utils.HTTPUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.Web3jUtil;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TransactionTest {

    @Test
    public void test() throws IOException {
        URL url = new URL("http://api.etherscan.io/api?module=account&action=txlist&" +
                "address=0x91164cba5f62df3f0f20606ab0124b13c2e2029e&sort=asc&apikey=YourApiKeyToken");
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
            TxPojo txPojo = JsonUtils.jsonToPojo(sb.toString(), TxPojo.class);
            for (TxBean bean : txPojo.getResult()) {
                String timeStamp = bean.getTimeStamp();
                long l = new Long(timeStamp) * 1000;
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(l);
                System.out.println(sf.format(date));
            }
        } else {
            System.out.println("error");
        }

    }


    @Test
    public void test4() throws IOException {
        AddressEncoder addressEncoder = new AddressEncoder("");
    }

    @Test
    public void test3() throws IOException {
        Web3j web3j = Web3jUtil.getWeb3j();
        Request<?, EthBlock> ethBlockRequest = web3j.ethGetBlockByHash(
                "0x10a939425f627158fc6ee73a6c6884fcadeacc7340da015f055890538a802a99",
                true);
        EthBlock send = ethBlockRequest.send();
        send.getResult();
        System.out.println();
    }

    @Test
    public void fun() {
        List<Price> list = HTTPUtils.getList("http://120.79.165.113:9099/getPrice", Price.class);
        list.forEach(System.out::println);
    }

}
