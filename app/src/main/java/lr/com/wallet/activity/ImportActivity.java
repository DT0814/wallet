package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import org.bitcoinj.core.Coin;

import java.util.Arrays;

import lr.com.wallet.R;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ETHWalletUtils;

/**
 * Created by dt0814 on 2018/7/14.
 */

public class ImportActivity extends Activity {
    private EditText editText;
    private Context context;
    private ETHWallet ethWallet;
    private EditText passWord;
    private EditText reImportPassword;
    private EditText importWalletName;
    private ImageButton importPreBut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_wallet_layout);
        RadioGroup rd = findViewById(R.id.radioGroup);
        editText = findViewById(R.id.importInPut);
        passWord = findViewById(R.id.importPassword);
        reImportPassword = findViewById(R.id.reImportPassword);
        importWalletName = findViewById(R.id.importWalletName);
        context = getBaseContext();
        AppFilePath.init(context);
        importPreBut = findViewById(R.id.importPreBut);
        importPreBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImportActivity.this.finish();
            }
        });
        rd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                switch (checkId) {
                    case R.id.keyStoreRadio:
                        System.out.println("keyStore");
                        editText.setHint("输入keyStroe文本");
                        reImportPassword.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.prvRadio:
                        editText.setHint("输入明文私钥");
                        reImportPassword.setVisibility(View.VISIBLE);

                        System.out.println("prvRadio");
                        break;
                    case R.id.WordRadio:
                        editText.setHint("输入助记词");
                        reImportPassword.setVisibility(View.VISIBLE);
                        System.out.println("WordRadio");
                        break;
                }
            }
        });

        Button button = findViewById(R.id.importButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int buttonId = rd.getCheckedRadioButtonId();
                String passString = passWord.getText().toString();
                String repassString = reImportPassword.getText().toString();
                switch (buttonId) {
                    case R.id.keyStoreRadio:
                        ethWallet = ETHWalletUtils.loadWalletByKeystore(editText.getText().toString(),
                                passString,
                                importWalletName.getText().toString());
                        WalletDao.writeCurrentJsonWallet(ethWallet);
                        WalletDao.writeJsonWallet(ethWallet);
                        addETH();
                        startActivity(new Intent(ImportActivity.this, MainFragmentActivity.class));
                        break;
                    case R.id.prvRadio:
                        if (!passString.equals(repassString)) {
                            Toast.makeText(ImportActivity.this, "两次密码输入不一致!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ethWallet = ETHWalletUtils.loadWalletByPrivateKey("0x" + editText.getText().toString(),
                                passString,
                                importWalletName.getText().toString());
                        WalletDao.writeCurrentJsonWallet(ethWallet);
                        WalletDao.writeJsonWallet(ethWallet);
                        addETH();
                        startActivity(new Intent(ImportActivity.this, MainFragmentActivity.class));
                        break;
                    case R.id.WordRadio:
                        if (!passString.equals(repassString)) {
                            Toast.makeText(ImportActivity.this, "两次密码输入不一致!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //"whisper lock park tomato reward fossil save dune item pottery tired apart"
                        ethWallet = ETHWalletUtils.importMnemonic(ETHWalletUtils.ETH_JAXX_TYPE,
                                Arrays.asList(editText.getText().toString().split(" ")),
                                passString,
                                importWalletName.getText().toString());
                        WalletDao.writeCurrentJsonWallet(ethWallet);
                        WalletDao.writeJsonWallet(ethWallet);
                        addETH();
                        startActivity(new Intent(ImportActivity.this, MainFragmentActivity.class));
                        break;
                }

            }
        });
    }

    private void addETH() {
        CoinPojo coinPojo = CoinDao.writeETHConinPojo();
    }

}
