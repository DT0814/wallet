package lr.com.wallet.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.HashMap;
import java.util.Map;

import lr.com.wallet.R;
import lr.com.wallet.activity.fragment.HangQingFragment;
import lr.com.wallet.activity.fragment.HomeFragment;
import lr.com.wallet.activity.fragment.InfoFragment;
import lr.com.wallet.activity.fragment.NoHaveWalletFragment;
import lr.com.wallet.activity.fragment.WalletFragment;
import lr.com.wallet.dao.CacheWalletDao;

public class MainFragmentActivity extends FragmentActivity implements View.OnClickListener {
    //底部导航切换按钮
    private ImageView mainBut;
    private ImageView infBut;
    private ImageView hangqing;
    private ImageView walletBut;
    //底部导航功能提示文字
    private TextView homeTextView;
    private TextView walletTextView;
    private TextView hangqingTextView;
    private TextView infoTextView;
    //    //底部Fragment碎片
//    private List<Fragment> fragments;
    //用户是否有钱包(用于判断主页面显示哪个)
    private boolean haveWallet = false;

    private Map<Tab, Fragment> fragmentMap;

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
        haveWallet = CacheWalletDao.haveWallet();
        fragmentMap = new HashMap<>();
        fragmentMap.put(Tab.nowallet, new NoHaveWalletFragment());
        fragmentMap.put(Tab.main, new HomeFragment(MainFragmentActivity.this));
        fragmentMap.put(Tab.wallet, new WalletFragment());
        fragmentMap.put(Tab.price, new HangQingFragment());
        fragmentMap.put(Tab.my, new InfoFragment());

        setContentView(R.layout.main_fragment_layout);

        //android获取文件读写权限
        requestAllPower();
        //初始化控件
        mainBut = findViewById(R.id.main);
        infBut = findViewById(R.id.info);
        walletBut = findViewById(R.id.wallet);
        hangqing = findViewById(R.id.hangqing);

        homeTextView = findViewById(R.id.homeTextView);
        walletTextView = findViewById(R.id.walletTextView);
        hangqingTextView = findViewById(R.id.hangqingTextView);
        infoTextView = findViewById(R.id.infoTextView);

        //初始化底部点击布局文件
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        LinearLayout infLayout = findViewById(R.id.infoLayout);
        LinearLayout hangqingLayout = findViewById(R.id.hangqingLayout);
        LinearLayout walletLayout = findViewById(R.id.walletLayout);
        //设置点击事件
        mainLayout.setOnClickListener(this);
        infLayout.setOnClickListener(this);
        hangqingLayout.setOnClickListener(this);
        walletLayout.setOnClickListener(this);

        //设置当前Fragment
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Log.i("position", position + "");

        onClick(mainLayout);
    }

    enum Tab {
        nowallet, main, wallet, price, my
    }

    /**
     * 底部导航区域点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.mainLayout:
                if (CacheWalletDao.getCurrentWallet() == null) {
                    onTabSelected(Tab.nowallet);
                } else {
                    onTabSelected(Tab.main);
                }
                break;
            case R.id.walletLayout:
                onTabSelected(Tab.wallet);
                break;
            case R.id.infoLayout:
                onTabSelected(Tab.my);
                break;
            case R.id.hangqingLayout:
                onTabSelected(Tab.price);
                break;
        }
    }

    /**
     * 请求去权限
     */
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

    //初始化底部导航为未选中状态
    private void initMenu() {
        findViewById(R.id.bgLayout).setBackgroundResource(R.color.colorPrimary);
        mainBut.setImageResource(R.drawable.main_home_off);
        homeTextView.setTextColor(getResources().getColor(R.color.navigationOffClolor, null));
        infBut.setImageResource(R.drawable.main_info_off);
        infoTextView.setTextColor(getResources().getColor(R.color.navigationOffClolor, null));
        walletBut.setImageResource(R.drawable.main_wallet_off);
        walletTextView.setTextColor(getResources().getColor(R.color.navigationOffClolor, null));
        hangqing.setImageResource(R.drawable.main_hangqing_off);
        hangqingTextView.setTextColor(getResources().getColor(R.color.navigationOffClolor, null));
    }


    android.support.v4.app.FragmentManager manager = getSupportFragmentManager();

    //设置当前显示的Fragment
    private void showFragment(Tab tab) {
        Fragment fragment = fragmentMap.get(tab);
        android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
        if (!manager.getFragments().contains(fragment)) {
            ft.add(R.id.frame, fragment);
        }
        for (Fragment f : fragmentMap.values()) {
            ft.hide(f);
        }
        ft.show(fragment);
        ft.commit();
//        android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
//        if (!manager.getFragments().contains(fragments.get(position))) {
//            ft.add(R.id.frame, fragments.get(position));
//        }
//        ft.hide(fragments.get(0));
//        ft.hide(fragments.get(1));
//        ft.hide(fragments.get(2));
//        ft.hide(fragments.get(3));
//        ft.show(fragments.get(position));
//        ft.commit();
    }

    private void onTabSelected(Tab tab) {
        showFragment(tab);
        initMenu();
        switch (tab) {
            case nowallet:
            case main:
                if (haveWallet) {
                    findViewById(R.id.bgLayout).setBackgroundResource(R.drawable.zichanbg);
                }
                homeTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                mainBut.setImageResource(R.drawable.main_home_on);
                break;
            case wallet:
                walletTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                walletBut.setImageResource(R.drawable.main_wallet_on);
                break;
            case price:
                hangqingTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                hangqing.setImageResource(R.drawable.main_hangqing_on);
                break;
            case my:
                infoTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                infBut.setImageResource(R.drawable.main_info_on);
                break;
        }
    }

    //点击item时跳转不同的Fragment碎片
//    private void onTabSelected(int position) {
//        showFragment(position);
//        switch (position) {
//            case 0:
//                initMenu();
//                if (haveWallet) {
//                    findViewById(R.id.bgLayout).setBackgroundResource(R.drawable.zichanbg);
//                }
//                homeTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
//                mainBut.setImageResource(R.drawable.main_home_on);
//                break;
//            case 1:
//                initMenu();
//                walletTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
//                walletBut.setImageResource(R.drawable.main_wallet_on);
//                break;
//            case 2:
//                initMenu();
//                hangqingTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
//                hangqing.setImageResource(R.drawable.main_hangqing_on);
//                break;
//            case 3:
//                initMenu();
//                infoTextView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
//                infBut.setImageResource(R.drawable.main_info_on);
//                break;
//        }
//    }


}
