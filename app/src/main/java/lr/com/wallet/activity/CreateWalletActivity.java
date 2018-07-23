package lr.com.wallet.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.CoinAdapter;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by dt0814 on 2018/7/12.
 */

public class CreateWalletActivity extends Activity {
    private TextView pass;
    private TextView repass;
    private TextView walletName;
    private Button but;
    private Button importBut;
    private ImageButton createPreBut;
    private Context context;
    private LayoutInflater inflater;
    private AlertDialog.Builder alertbBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_wallet_layout);
        inflater = this.getLayoutInflater();
        pass = this.findViewById(R.id.inPass);
        repass = this.findViewById(R.id.rePass);
        walletName = this.findViewById(R.id.walletName);
        but = this.findViewById(R.id.createBut);
        importBut = this.findViewById(R.id.importBut);
        context = this.getBaseContext();
        AppFilePath.init(context);
        createPreBut = findViewById(R.id.createPreBut);
        alertbBuilder = new AlertDialog.Builder(CreateWalletActivity.this);
        createPreBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateWalletActivity.this.finish();
            }
        });
        importBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateWalletActivity.this, ImportActivity.class));
            }
        });

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pasStr = pass.getText().toString();
                final String repassStr = repass.getText().toString();
                final String walletNameStr = walletName.getText().toString();

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
                if (null == walletNameStr || walletNameStr.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateWalletActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("钱包名称不能为空");
                    builder.setPositiveButton("是", null);
                    builder.show();
                    return;
                }

                //android获取文件读写权限
                requestAllPower();

                ETHWalletUtils ethWalletUtils = new ETHWalletUtils();
                ETHWallet ethWallet = ethWalletUtils.generateMnemonic(walletNameStr, repassStr);

                View showMnemonicLayout = inflater.inflate(R.layout.show_mnemonic_layout, null);
                TextView show = showMnemonicLayout.findViewById(R.id.showMenemonic);
                show.setText(ethWallet.getMnemonic());
                alertbBuilder.setView(showMnemonicLayout);
                alertbBuilder.setTitle("助记词").setMessage("").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                WalletDao.writeJsonWallet(ethWallet);
                                WalletDao.writeCurrentJsonWallet(ethWallet);
                                CoinPojo coinPojo = CoinDao.writeETHConinPojo();
                                System.out.println(coinPojo);
                                Intent intent = new Intent(CreateWalletActivity.this, MainFragmentActivity.class);
                                intent.putExtra("storePass", ethWallet.getKeystorePath());
                                startActivity(intent);
                            }
                        }).create();
                alertbBuilder.show();
            }
        });
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

}
