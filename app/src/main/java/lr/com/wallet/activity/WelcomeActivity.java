package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hunter.wallet.service.EthWallet;
import com.hunter.wallet.service.SecurityService;
import com.hunter.wallet.service.TeeErrorException;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ConvertPojo;
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
        SharedPreferencesUtils.init(context);
        AppFilePath.init(context);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ETHWallet ethWallet = WalletDao.getCurrentWallet();
                List<EthWallet> walletList = null;
                try {
                    walletList = SecurityService.getWalletList();
                } catch (TeeErrorException e) {
                    e.printStackTrace();
                }
                if (null != walletList && walletList.size() > 0 && WalletDao.getAllWallet().size() != walletList.size()) {
                    for (ETHWallet wallet : WalletDao.getAllWallet()) {
                        WalletDao.deleteWallet(wallet);
                    }

                    for (EthWallet wallet : walletList) {
                        ETHWallet ethWallet2 = ConvertPojo.toETHWallet(wallet);
                        WalletDao.writeJsonWallet(ethWallet2);
                        CoinDao.writeETHConinPojo(ethWallet2);
                    }
                    ethWallet = ConvertPojo.toETHWallet(walletList.get(0));
                    WalletDao.writeCurrentJsonWallet(ethWallet);
                }
                startActivity(new Intent(WelcomeActivity.this, MainFragmentActivity.class));
                WelcomeActivity.this.finish();
            }
        }, 1500);

    }
}
