package lr.com.wallet.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import lr.com.wallet.R;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.SharedPreferencesUtils;

public class MainFragmentActivity extends FragmentActivity implements View.OnClickListener {
    private Context context;
    private ImageButton mainBut;
    private ImageButton infBut;
    private ImageButton walletBut;
    private ETHWallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_layout);
        context = getBaseContext();
        SharedPreferencesUtils.init(context);
        //android获取文件读写权限
        requestAllPower();
        mainBut = findViewById(R.id.main);
        infBut = findViewById(R.id.info);
        walletBut = findViewById(R.id.wallet);
        context = this.getBaseContext();
        wallet = WalletDao.getCurrentWallet();
        if (null == wallet) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
            normalDialog.setTitle("提示");
            normalDialog.setMessage("您还没有钱包");
            normalDialog.setPositiveButton("导入钱包",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainFragmentActivity.this, ImportActivity.class);
                            startActivity(intent);
                        }
                    });
            normalDialog.setNegativeButton("创建钱包",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainFragmentActivity.this, CreateWalletActivity.class);
                            startActivity(intent);
                        }
                    });
            normalDialog.show();
        } else {
            System.out.println(wallet);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commitAllowingStateLoss();
        }

        mainBut.setOnClickListener(this);
        infBut.setOnClickListener(this);
        walletBut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main:
                initMenu();
                mainBut.setImageResource(R.drawable.home_on);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commitAllowingStateLoss();
                break;
            case R.id.wallet:
                initMenu();
                walletBut.setImageResource(R.drawable.wallet_on);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new WalletFragment()).commitAllowingStateLoss();
                break;
            case R.id.info:
                initMenu();
                infBut.setImageResource(R.drawable.info_on);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new InfoFragment()).commitAllowingStateLoss();
                break;
        }
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initMenu() {
        mainBut.setImageResource(R.drawable.home_off);
        infBut.setImageResource(R.drawable.info_off);
        walletBut.setImageResource(R.drawable.wallet_off);
    }
}
