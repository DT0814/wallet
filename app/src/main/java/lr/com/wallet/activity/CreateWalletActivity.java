package lr.com.wallet.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityErrorException;
import com.hunter.wallet.service.SecurityUtils;

import lr.com.wallet.R;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ConvertPojo;

/**
 * Created by dt0814 on 2018/7/12.
 */

public class CreateWalletActivity extends Activity {
    private TextView pass;
    private TextView repass;
    private TextView walletName;
    private Button createBut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_wallet_layout);
        pass = this.findViewById(R.id.inPass);
        repass = this.findViewById(R.id.rePass);
        walletName = this.findViewById(R.id.walletName);
        createBut = this.findViewById(R.id.createBut);
        Context context = this.getBaseContext();
        AppFilePath.init(context);
        findViewById(R.id.createPreBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
   /*             Intent intent = new Intent(CreateWalletActivity.this, MainFragmentActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);*/
                CreateWalletActivity.this.finish();
            }
        });

        walletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    if (!walletName.getText().toString().trim().equals("")) {
                        createBut.setEnabled(true);
                        createBut.setBackgroundResource(R.drawable.create_but_fill);
                    }
                }
            }
        });
        createBut.setOnClickListener(new View.OnClickListener() {
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

                //  ETHCacheWallet ethWallet = ethWalletUtils.generateMnemonic(ETHWalletUtils.ETH_JAXX_TYPE, walletNameStr, repassStr);

                try {
                    Log.i("创建钱包参数", walletNameStr + "     " + repassStr);
                    ETHCacheWallet ethCacheWallet = ConvertPojo.toETHCacheWallet(SecurityUtils.createWallet(walletNameStr, repassStr));
                    CacheWalletDao.deleteCache(ethCacheWallet);
                    Log.i("创建完成的钱包", ethCacheWallet.toString());
                    String mne = SecurityUtils.getMnemonic(ethCacheWallet.getId().intValue(), repassStr);
                    CacheWalletDao.writeJsonWallet(ethCacheWallet);
                    CacheWalletDao.writeCurrentJsonWallet(ethCacheWallet);
                    CoinPojo coinPojo = CoinDao.writeETHConinPojo();
                    Intent intent = new Intent(CreateWalletActivity.this, CreateShowMnemonicActivity.class);
                    intent.putExtra("mnemonic", mne);
                    startActivity(intent);
                    CreateWalletActivity.this.finish();
                } catch (SecurityErrorException e) {
                    e.printStackTrace();
                    if (e.getErrorCode() == SecurityErrorException.ERROR_WALLET_AMOUNT_CROSS) {
                        Toast.makeText(CreateWalletActivity.this, "超出钱包数量限制", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
//        ReminderUtils.showReminder(CreateWalletActivity.this);

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
