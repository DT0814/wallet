package lr.com.wallet.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lr.com.wallet.pojo.TransactionBean;
import lr.com.wallet.pojo.TransactionPojo;
import lr.com.wallet.utils.JsonUtils;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TransactionTest {
    @Test
    public void test2() {
        List list = new ArrayList();
        TransactionBean transactionBean = new TransactionBean();
        transactionBean.setBlockNumber("2131232");
        list.add(transactionBean);
        list.add(transactionBean);
        list.add(transactionBean);
        list.add(transactionBean);
        list.add(transactionBean);
        String s = JsonUtils.objectToJson(list);
        System.out.println(s);
        List<TransactionBean> beans = JsonUtils.jsonToList(s, TransactionBean.class);
        for (TransactionBean date : beans) {
            System.out.println(date);
        }


    }

    @Test
    public void test() throws IOException {
        URL url = new URL("http://api.etherscan.io/api?module=account&action=txlist&" +
                "address=0x59Dd7DfB072c1C80CeEc4d08588A01603C5d3bf0&sort=asc&apikey=YourApiKeyToken");
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
            System.out.println(transactionPojo);
            for (TransactionBean bean : transactionPojo.getResult()) {
                String timeStamp = bean.getTimeStamp();
                long l = new Long(timeStamp) * 1000;
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(l);
                System.out.println(sf.format(date));
                System.out.println(bean);
            }
        } else {
            System.out.println("error");
        }

    }


}
