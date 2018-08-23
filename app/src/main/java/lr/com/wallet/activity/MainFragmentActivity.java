package lr.com.wallet.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.fragment.HangQingFragment;
import lr.com.wallet.activity.fragment.HomeFragment;
import lr.com.wallet.activity.fragment.InfoFragment;
import lr.com.wallet.activity.fragment.NoHaveWalletFragment;
import lr.com.wallet.activity.fragment.WalletFragment;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.Md5Utils;
import lr.com.wallet.utils.SharedPreferencesUtils;

public class MainFragmentActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView mainBut;
    private ImageView infBut;
    private ImageView hangqing;
    private ImageView walletBut;
    private LinearLayout mainLayout;
    private LinearLayout infLayout;
    private LinearLayout hangqingLayout;
    private LinearLayout walletLayout;

    private ETHWallet ethWallet;
    private List<Fragment> fragments;
    private TextView homeTextView;
    private TextView walletTextView;
    private TextView hangqingTextView;
    private TextView infoTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android5.0之后的沉浸式状态栏写法
        Window window = this.getWindow();
        View decorView = window.getDecorView();
        // 两个标志位要结合使用，表示让应用的主体内容占用系统状态栏的空间
        // 第三个标志位可让底部导航栏变透明View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        decorView.setSystemUiVisibility(option);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        setContentView(R.layout.main_fragment_layout);
        Context context = getBaseContext();
        SharedPreferencesUtils.init(context);
        AppFilePath.init(context);
        //android获取文件读写权限
        requestAllPower();
        mainBut = findViewById(R.id.main);
        infBut = findViewById(R.id.info);
        walletBut = findViewById(R.id.wallet);
        hangqing = findViewById(R.id.hangqing);

        mainLayout = findViewById(R.id.mainLayout);
        infLayout = findViewById(R.id.infoLayout);
        hangqingLayout = findViewById(R.id.hangqingLayout);
        walletLayout = findViewById(R.id.walletLayout);

        homeTextView = findViewById(R.id.homeTextView);
        walletTextView = findViewById(R.id.walletTextView);
        hangqingTextView = findViewById(R.id.hangqingTextView);
        infoTextView = findViewById(R.id.infoTextView);


        ethWallet = WalletDao.getCurrentWallet();
        if (null == ethWallet) {
            NoHaveWalletFragment noHaveWalletFragment = new NoHaveWalletFragment();
            WalletFragment walletFragment = new WalletFragment();
            InfoFragment infoFragment = new InfoFragment();
            HangQingFragment hangQingFragment = new HangQingFragment();
            fragments = new ArrayList<>();
            fragments.add(noHaveWalletFragment);
            fragments.add(walletFragment);
            fragments.add(hangQingFragment);
            fragments.add(infoFragment);
        } else {
            HomeFragment homeFragment = new HomeFragment(MainFragmentActivity.this);
            WalletFragment walletFragment = new WalletFragment();
            InfoFragment infoFragment = new InfoFragment();
            HangQingFragment hangQingFragment = new HangQingFragment();
            fragments = new ArrayList<>();
            fragments.add(homeFragment);
            fragments.add(walletFragment);
            fragments.add(hangQingFragment);
            fragments.add(infoFragment);

        }
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Log.i("position", position + "");
        onTabSelected(position);
        mainLayout.setOnClickListener(this);
        infLayout.setOnClickListener(this);
        hangqingLayout.setOnClickListener(this);
        walletLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.mainLayout:
                onTabSelected(0);
                break;
            case R.id.walletLayout:
                onTabSelected(1);
                break;
            case R.id.infoLayout:
                onTabSelected(3);
                break;
            case R.id.hangqingLayout:
                onTabSelected(2);
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
        findViewById(R.id.bgLayout).setBackgroundResource(R.color.colorPrimary);
        mainBut.setImageResource(R.drawable.home_off);
        homeTextView.setTextColor(getResources().getColor(R.color.navigationOffClolor, null));
        infBut.setImageResource(R.drawable.info_off);
        infoTextView.setTextColor(getResources().getColor(R.color.navigationOffClolor, null));
        walletBut.setImageResource(R.drawable.wallet_off);
        walletTextView.setTextColor(getResources().getColor(R.color.navigationOffClolor, null));
        hangqing.setImageResource(R.drawable.hangqing_off);
        hangqingTextView.setTextColor(getResources().getColor(R.color.navigationOffClolor, null));
    }


    android.support.v4.app.FragmentManager manager = getSupportFragmentManager();

    private void showFragment(int position) {

        android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
        if (!manager.getFragments().contains(fragments.get(position))) {
            ft.add(R.id.frame, fragments.get(position));
        }
        ft.hide(fragments.get(0));
        ft.hide(fragments.get(1));
        ft.hide(fragments.get(2));
        ft.hide(fragments.get(3));
        ft.show(fragments.get(position));
        ft.commit();
    }

    //点击item时跳转不同的碎片
    private void onTabSelected(int position) {
        showFragment(position);
        switch (position) {
            case 0:
                initMenu();
                findViewById(R.id.bgLayout).setBackgroundResource(R.drawable.zichanbg);
                homeTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                mainBut.setImageResource(R.drawable.home_on);
                break;
            case 1:
                initMenu();
                walletTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                walletBut.setImageResource(R.drawable.wallet_on);
                break;
            case 2:
                initMenu();
                hangqingTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                hangqing.setImageResource(R.drawable.hangqing_on);
                break;
            case 3:
                initMenu();
                infoTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                infBut.setImageResource(R.drawable.info_on);
                break;
        }
    }
}
