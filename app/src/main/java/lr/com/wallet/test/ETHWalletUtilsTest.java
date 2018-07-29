package lr.com.wallet.test;

/**
 * Created by dt0814 on 2018/7/13.
 */

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.ETHWalletUtils;


public class ETHWalletUtilsTest {
    @Test
    public void inputTest() throws IOException {
       /* ETHWallet wo = ETHWalletUtils.loadWalletByKeystore("/storage/emulated/0/ethtoken/keystore_tt.json", "woaini.1314");
        String address = wo.getAddress();
        System.out.println(address);*/
        File file = new File("storage/emulated/0/ethtoken/keystore_tt.json");
        FileInputStream inputStream = new FileInputStream(file);
        byte[] b = new byte[1024];
        inputStream.read(b);
    }

    @Test
    public void test() {
    }

    @Test
    public void test2() throws Exception {
    }
}
