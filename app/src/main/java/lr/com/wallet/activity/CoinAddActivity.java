package lr.com.wallet.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.CoinAdapter;
import lr.com.wallet.adapter.CoinAddAdapter;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.CoinUtils;
import lr.com.wallet.utils.JsonUtils;

/**
 * Created by dt0814 on 2018/7/23.
 */

public class CoinAddActivity extends Activity {
    private ListView listView;
    private ETHWallet ethWallet;
    private CoinPojo coin;
    private LayoutInflater inflater;
    private ImageButton addressInfoPreBut;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_add_layout);
        inflater = this.getLayoutInflater();
        listView = findViewById(R.id.addCoinListView);
        ethWallet = WalletDao.getCurrentWallet();
        addressInfoPreBut = findViewById(R.id.addressInfoPreBut);
        addressInfoPreBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoinAddActivity.this.finish();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CoinPojo> list = new ArrayList();
                coin = new CoinPojo();
                coin.setCoinAddress("0x9d1fa651bf92043f26afdbca3a0548983d76ace5");
                String name = CoinUtils.getName("0x9d1fa651bf92043f26afdbca3a0548983d76ace5");
                String symbolName = CoinUtils.getSymbolName("0x9d1fa651bf92043f26afdbca3a0548983d76ace5");
                coin.setCoinName(name);
                coin.setCoinSymbolName(symbolName);
                coin.setWalletId(ethWallet.getId());
                list.add(coin);
                CoinAddAdapter adapter = new CoinAddAdapter(getBaseContext(), R.layout.coin_add_list_view, list);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                CoinPojo coinPojo = CoinDao.addConinPojo(coin);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        View homeView = inflater.inflate(R.layout.home_fragment, null);
                                        ListView coinListView = homeView.findViewById(R.id.coinListView);
                                        System.out.println(coinListView);
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                List<CoinPojo> coinPojos = CoinDao.getConinListByWalletId(ethWallet.getId());
                                                CoinAdapter adapter = new CoinAdapter(CoinAddActivity.this, R.layout.coin_list_view, coinPojos);
                                                coinListView.setAdapter(adapter);
                                                coinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                        CoinPojo itemAtPosition = (CoinPojo) adapterView.getItemAtPosition(i);
                                                        Intent intent = new Intent(CoinAddActivity.this, CoinInfoActivity.class);
                                                        intent.putExtra("obj", JsonUtils.objectToJson(itemAtPosition));
                                                        System.out.println(itemAtPosition);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }).start();
                                Intent intent = new Intent(CoinAddActivity.this, MainFragmentActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });

            }
        }).start();
    }
}
