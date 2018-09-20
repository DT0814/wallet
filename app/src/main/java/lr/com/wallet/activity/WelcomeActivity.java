package lr.com.wallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.hunter.wallet.service.UserInfo;
import com.hunter.wallet.service.WalletInfo;
import com.hunter.wallet.service.SecurityUtils;
import com.hunter.wallet.service.SecurityErrorException;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ConvertPojo;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by DT0814 on 2018/8/22.
 */

public class WelcomeActivity extends FragmentActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        Context context = getBaseContext();
        //初始化SharedPreferences
        SharedPreferencesUtils.init(context);
        AppFilePath.init(context);
        SecurityUtils.init(context);
        new Handler().postDelayed(new Runnable() {
            /**
             * 检查用户钱包缓存和tee环境钱包缓存是否匹配 不匹配同步数据
             */
            @Override
            public void run() {
                try {
                    UserInfo userInfo = SecurityUtils.getUserInfo();
                    Log.i("userInfo", userInfo.toString());
                    if (!userInfo.isHasInit()) {
                        startActivity(new Intent(WelcomeActivity.this, InitActivity.class));
                        WelcomeActivity.this.finish();
                        return;
                    }
                } catch (SecurityErrorException e) {
                    e.printStackTrace();
                }
                List<WalletInfo> walletList = null;
                try {
                    walletList = SecurityUtils.getWalletList();
                    if (null != walletList && walletList.size() < 1) {
                        CacheWalletDao.clean();
                    }

                } catch (SecurityErrorException e) {
                    e.printStackTrace();
                }
                if (null != walletList && walletList.size() > 0 && CacheWalletDao.getAllWallet().size() != walletList.size()) {
                    for (ETHCacheWallet wallet : CacheWalletDao.getAllWallet()) {
                        CacheWalletDao.deleteWallet(wallet);
                    }

                    for (WalletInfo wallet : walletList) {
                        ETHCacheWallet ethCacheWallet2 = ConvertPojo.toETHCacheWallet(wallet);
                        CacheWalletDao.writeJsonWallet(ethCacheWallet2);
                        CoinDao.writeETHConinPojo(ethCacheWallet2);
                    }
                    ETHCacheWallet ethCacheWallet = ConvertPojo.toETHCacheWallet(walletList.get(0));
                    CacheWalletDao.writeCurrentJsonWallet(ethCacheWallet);
                }
                startActivity(new Intent(WelcomeActivity.this, MainFragmentActivity.class));
                WelcomeActivity.this.finish();
            }
        }, 1500);

    }
}
