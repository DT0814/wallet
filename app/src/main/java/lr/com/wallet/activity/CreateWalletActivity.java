package lr.com.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

import static org.web3j.crypto.WalletUtils.generateWalletFile;

/**
 * Created by dt0814 on 2018/7/12.
 */

public class CreateWalletActivity extends AppCompatActivity {
    private TextView pass;
    private TextView repass;
    private Button but;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        pass = this.findViewById(R.id.inPass);
        repass = this.findViewById(R.id.rePass);
        but = this.findViewById(R.id.createBut);
        context = this.getBaseContext();
        but.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                String pasStr = pass.getText().toString();
                String repassStr = repass.getText().toString();
                if (pasStr.length() < 6) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateWalletActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("密码不能少于6位");
                    builder.setPositiveButton("是", null);
                    builder.show();
                    return;
                }
                if (!repassStr.equals(pasStr)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateWalletActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("两次输入密码不一致");
                    builder.setPositiveButton("是", null);
                    builder.show();
                    return;
                }
                WalletPojo wallet = new WalletPojo();
                try {
                    // ECKeyPair ecKeyPair = Keys.createEcKeyPair();
                    //generateWalletFile(repassStr, ecKeyPair, new File(""), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                seedToPasswd(makeSeed(getWord(wallet)), wallet, repassStr);
                SharedPreferences sf = context.getSharedPreferences("walletPojo", MODE_PRIVATE);
                WalletPojoUtil.write(sf, wallet);
                startActivity(new Intent(CreateWalletActivity.this, MainActivity.class));
            }
        });
    }

    private static List<String> getWord(WalletPojo wallet) {
        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];

        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE)
                .createMnemonic(entropy, sb::append);
        System.out.println("助记词：" + sb);
        wallet.setWords(sb.toString());
        List<String> words = new ArrayList<>();
        String[] arr = sb.toString().split(" ");

        for (String str : arr) {
            words.add(str);
        }
        return words;
    }

    private static byte[] makeSeed(List<String> words) {
        byte[] seed = new SeedCalculator()
                .withWordsFromWordList(English.INSTANCE)
                .calculateSeed(words, "");

        return seed;

    }

    public static void seedToPasswd(byte[] seeds, WalletPojo wallet, String repassStr) {
        //这个SHA256可以使用 scryt-1.4.0
        ECKeyPair ecKeyPair = ECKeyPair.create(SHA256Util.getSHA256StrJava(seeds));
        String privateKey = ecKeyPair.getPrivateKey().toString(16);
        String publicKey = ecKeyPair.getPublicKey().toString(16);
        wallet.setPrvKey(privateKey);
        wallet.setPubKey(publicKey);

        System.out.println("私钥:" + privateKey);
        System.out.println("公钥:" + publicKey);
       /* try {
            Credentials credentials = WalletUtils.loadCredentials("",
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/UTC--2017-08-21T11-49-30.013Z--8c17ea160c092ae854f81580396ba570d9e62e24.json");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }*/
        try {
            WalletFile walletFile = Wallet.create("123456", ecKeyPair, 2, 1);
            System.out.println("钱包地址:" + walletFile.getAddress());
            wallet.setAddress(walletFile.getAddress());

        } catch (CipherException e) {
            e.printStackTrace();
        }

    }
}
