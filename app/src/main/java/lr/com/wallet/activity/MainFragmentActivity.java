package lr.com.wallet.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.Md5Utils;
import lr.com.wallet.utils.SharedPreferencesUtils;

public class MainFragmentActivity extends FragmentActivity implements View.OnClickListener {
    private Context context;
    private ImageButton mainBut;
    private ImageButton infBut;
    private ImageButton walletBut;
    private ETHWallet ethWallet;
    private HomeFragment homeFragment;
    private WalletFragment walletFragment;
    private InfoFragment infoFragment;
    private ImageButton mainMenuBut;
    private ClipboardManager clipManager;
    private LayoutInflater inflater;
    private ClipData mClipData;
    private PopupMenu popupMenu;
    private Menu menu;
    private AlertDialog.Builder alertbBuilder;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_layout);
        context = getBaseContext();
        SharedPreferencesUtils.init(context);
        inflater = getLayoutInflater();
        alertbBuilder = new AlertDialog.Builder(this);
        //android获取文件读写权限
        requestAllPower();
        mainBut = findViewById(R.id.main);
        infBut = findViewById(R.id.info);
        walletBut = findViewById(R.id.wallet);
        mainMenuBut = findViewById(R.id.mainMenuBtn);
        popupMenu = new PopupMenu(this, mainMenuBut);
        menu = popupMenu.getMenu();
        // 通过XML文件添加菜单项
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        clipManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                View pwdView;
                switch (item.getItemId()) {
                    case R.id.copyPrvKey:
                        pwdView = inflater.inflate(R.layout.input_pwd_layout, null);
                        alertbBuilder.setView(pwdView);
                        alertbBuilder.setTitle("请输入密码").setMessage("").setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditText editText = pwdView.findViewById(R.id.inPwdBut);
                                        String pwd = editText.getText().toString();
                                        String privateKey = ETHWalletUtils.derivePrivateKey(ethWallet, pwd);
                                        if (null == privateKey || privateKey.equals("")) {
                                            Toast.makeText(MainFragmentActivity.this, "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mClipData = ClipData.newPlainText("Label", privateKey);
                                            clipManager.setPrimaryClip(mClipData);
                                            Toast.makeText(MainFragmentActivity.this, "私钥已经复制到剪切板", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                        }
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }

                        }).create();
                        alertbBuilder.show();
                        break;
                    case R.id.copyWalletAddress:
                        mClipData = ClipData.newPlainText("Label", ethWallet.getAddress());
                        clipManager.setPrimaryClip(mClipData);
                        Toast.makeText(MainFragmentActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.copyKeyStore:

                        pwdView = inflater.inflate(R.layout.input_pwd_layout, null);
                        alertbBuilder.setView(pwdView);
                        alertbBuilder.setTitle("请输入密码").setMessage("").setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditText editText = pwdView.findViewById(R.id.inPwdBut);
                                        String pwd = editText.getText().toString();
                                        if (!ethWallet.getPassword().equals(Md5Utils.md5(pwd))) {
                                            editText.setText("");
                                            Toast.makeText(MainFragmentActivity.this, "密码错误重新输入", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        String keyStoreStr = ETHWalletUtils.deriveKeystore(ethWallet);
                                        mClipData = ClipData.newPlainText("Label", keyStoreStr);
                                        clipManager.setPrimaryClip(mClipData);
                                        Toast.makeText(MainFragmentActivity.this, "keyStore复制到剪切板", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                        alertbBuilder.show();
                        break;
                }
                return false;
            }
        });


        context = this.getBaseContext();
        ethWallet = WalletDao.getCurrentWallet();
        homeFragment = new HomeFragment();
        walletFragment = new WalletFragment();
        infoFragment = new InfoFragment();
        if (null == ethWallet) {
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
            //getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commitAllowingStateLoss();
        }
        fragments = new ArrayList<>();
        fragments.add(homeFragment);
        fragments.add(walletFragment);
        fragments.add(infoFragment);


        //设置默认的碎片
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.frame, fragments.get(0));
        transaction.show(fragments.get(0));
        transaction.commit();

        mainBut.setOnClickListener(this);
        infBut.setOnClickListener(this);
        walletBut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main:
                initMenu();
                onTabSelected(0);
                mainBut.setImageResource(R.drawable.home_on);
                break;
            case R.id.wallet:
                initMenu();
                onTabSelected(1);
                walletBut.setImageResource(R.drawable.wallet_on);
                break;
            case R.id.info:
                initMenu();
                onTabSelected(2);
                infBut.setImageResource(R.drawable.info_on);
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


    public void popupmenu(View v) {
        popupMenu.show();
    }

    //点击item时跳转不同的碎片
    public void onTabSelected(int position) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
        for (Fragment f : manager.getFragments()) {
            System.out.print(f.toString()+"---------------------");
        }

        if (position == 0) {
            if (!manager.getFragments().contains(fragments.get(0))) {
                ft.add(R.id.frame, fragments.get(0));
            }
            ft.hide(fragments.get(1));
            ft.hide(fragments.get(2));
            ft.show(fragments.get(0));
            ft.commit();
        }
        if (position == 1) {
            if (!manager.getFragments().contains(fragments.get(1))) {
                ft.add(R.id.frame, fragments.get(1));
            }
            ft.hide(fragments.get(0));
            ft.hide(fragments.get(2));
            ft.show(fragments.get(1));
            ft.commit();
        }
        if (position == 2) {
            if (!manager.getFragments().contains(fragments.get(2))) {
                ft.add(R.id.frame, fragments.get(2));
            }
            ft.hide(fragments.get(0));
            ft.hide(fragments.get(1));
            ft.show(fragments.get(2));
            ft.commit();
        }
    }
}
