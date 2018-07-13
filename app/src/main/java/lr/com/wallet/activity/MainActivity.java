package lr.com.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;
import lr.com.wallet.R;
import lr.com.wallet.pojo.WalletPojo;
import lr.com.wallet.test.SHA256Util;
import lr.com.wallet.utils.WalletPojoUtil;

public class MainActivity extends AppCompatActivity {
    WalletPojo walletPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = this.findViewById(R.id.ethNum);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String model = (String) msg.obj;
                textView.setText(model);
            }
        };
        walletPojo = WalletPojoUtil.read(this.getBaseContext().getSharedPreferences("walletPojo", MODE_PRIVATE));
        System.out.println(walletPojo);

        final Intent intent = this.getIntent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Web3j web3 = Web3j.build(new HttpService("https://rinkeby.infura.io/c0oGHqQQlq6XJU2kz5DL"));
                //yWeb3j web3 = Web3j.build(new HttpService("https://mainnet.infura.io/c0oGHqQQlq6XJU2kz5DL"));
                try {
                    final Bundle extras = intent.getExtras();
                    // //获取余额
                    EthGetBalance ethGetBalance1 = web3.ethGetBalance("0x59dd7dfb072c1c80ceec4d08588a01603c5d3bf0",
                            DefaultBlockParameter.valueOf("latest")).send();
                    /*EthGetBalance ethGetBalance1 = web3.ethGetBalance("0x" + walletPojo.getAddress(),
                            DefaultBlockParameter.valueOf("latest")).send();*/
                    Message ms = new Message();
                    //System.out.println(ethGetBalance1.getError().getMessage());
                    System.out.println(ethGetBalance1.getBalance());
                    BigDecimal bigDecimal = Convert.fromWei(ethGetBalance1.getBalance().toString(), Convert.Unit.ETHER);
                    ms.obj = bigDecimal.toString();
                    handler.sendMessage(ms);

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }


}
