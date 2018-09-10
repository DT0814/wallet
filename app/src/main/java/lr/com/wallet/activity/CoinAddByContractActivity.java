package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by DT0814 on 2018/8/24.
 */

public class CoinAddByContractActivity extends Activity {
    private TextView coinAddressInput;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_add_by_contract_layout);
        coinAddressInput = findViewById(R.id.coinAddressInput);
        ETHCacheWallet ethCacheWallet = CacheWalletDao.getCurrentWallet();

        findViewById(R.id.coinAddByContractPreBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoinAddByContractActivity.this, CoinAddActivity.class);
                intent.putExtra("CoinPojos", getIntent().getStringExtra("CoinPojos"));
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.addCoinSaoyisaoBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CoinAddByContractActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //先判断有没有权限 ，没有就在这里进行权限的申请
                    ActivityCompat.requestPermissions(CoinAddByContractActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    startActivityForResult(new Intent(CoinAddByContractActivity.this, CaptureActivity.class), 0);
                }
            }
        });
        findViewById(R.id.addCoinBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = coinAddressInput.getText().toString();
                if (null == address || address.trim().length() != 42) {
                    Toast.makeText(CoinAddByContractActivity.this, "代币地址为以0x开头的长度为42的字符串", Toast.LENGTH_LONG).show();
                    return;
                }
                if (CoinDao.CheckContains(address, ethCacheWallet.getId())) {
                    Toast.makeText(CoinAddByContractActivity.this, "当前钱包已存在该代币请勿重复添加", Toast.LENGTH_LONG).show();
                    return;
                }
                String result = SharedPreferencesUtils.getString("ethCacheWallet", "coinList");
                List<CoinPojo> coinPojos = JsonUtils.jsonToList(result, CoinPojo.class);
                if (CoinDao.contain(address, coinPojos)) {
                    Toast.makeText(CoinAddByContractActivity.this, "请勿重复添加", Toast.LENGTH_LONG).show();
                    return;
                }
                CoinPojo coinPojo = CoinDao.getCoinPojoByAddress(address, ethCacheWallet.getAddress());
                if (null == coinPojo) {
                    Toast.makeText(CoinAddByContractActivity.this, "未查询到代币", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CoinAddByContractActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                    coinPojos.add(coinPojo);
                    SharedPreferencesUtils.writeString("ethCacheWallet", "coinList", JsonUtils.objectToJson(coinPojos));
                    Intent intent = new Intent(CoinAddByContractActivity.this, CoinAddActivity.class);
                    intent.putExtra("CoinPojos", getIntent().getStringExtra("CoinPojos"));
                    startActivity(intent);
                    finish();
                }


            }
        });
    }

    /**
     * 扫过二维码回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            coinAddressInput.setText(result);
        } else {
            Toast.makeText(CoinAddByContractActivity.this, "二维码解析失败", Toast.LENGTH_LONG).show();
        }
    }
}
