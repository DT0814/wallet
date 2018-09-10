package lr.com.wallet.test;

/**
 * Created by dt0814 on 2018/7/13.
 */

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.CoinUtils;
import lr.com.wallet.utils.ETHWalletUtils;


public class ETHWalletUtilsTest {
    @Test
    public void inputTest() throws IOException {
       /* ETHCacheWallet wo = ETHWalletUtils.loadWalletByKeystore("/storage/emulated/0/ethtoken/keystore_tt.json", "woaini.1314");
        String address = wo.getAddress();
        System.out.println(address);*/
        File file = new File("storage/emulated/0/ethtoken/keystore_tt.json");
        FileInputStream inputStream = new FileInputStream(file);
        byte[] b = new byte[1024];
        inputStream.read(b);
    }

    @Test
    public void test() {
        ETHWallet ethWallet = ETHWalletUtils.generateMnemonic(
                ETHWalletUtils.ETH_JAXX_TYPE, "土豪", "123456");
        ETHWallet ethWallet1 = ETHWalletUtils.importMnemonic(
                ETHWalletUtils.ETH_JAXX_TYPE, ethWallet.getMnemonic(), "123456", "importMnemonic");
        ETHWallet ethWallet2 = ETHWalletUtils.loadWalletByKeystore(
                ethWallet.getKeyStore(), "123456", "loadWalletByKeystore");
        ETHWallet ethWallet3 = ETHWalletUtils.loadWalletByPrivateKey(
                ETHWalletUtils.derivePrivateKey(ethWallet, "123456"), "123456", "loadWalletByPrivateKey");

        System.out.println();
    }

    @Test
    public void test2() throws Exception {
        String name = CoinUtils.getName("0x6f6eef16939b8327d53afdcaf08a72bba99c1a7f", "0x6de09c4040789a7dd75e7ae7482f98241515bc1b");
        System.out.println(name);
    }
}
