package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.CoinAddAdapter;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by dt0814 on 2018/7/23.
 */

public class CoinAddActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private ETHCacheWallet ethCacheWallet;
    private List<CoinPojo> coinPojos;
    Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_add_layout);
        listView = findViewById(R.id.addCoinListView);
        addHeader();
        ethCacheWallet = CacheWalletDao.getCurrentWallet();
        ImageButton addressInfoPreBut = findViewById(R.id.addressInfoPreBut);
        ImageButton coinAddSousuo = findViewById(R.id.coinAddSousuo);
        coinAddSousuo.setOnClickListener(this);
        addressInfoPreBut.setOnClickListener(this);
        String coinPojosJson = getIntent().getStringExtra("CoinPojos");
        if (null != coinPojosJson) {
            coinPojos = JsonUtils.jsonToList(coinPojosJson, CoinPojo.class);
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                CoinAddAdapter adapter = (CoinAddAdapter) msg.obj;
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //跳转至显示信息页面
                    }
                });
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                CoinAddAdapter adapter = new CoinAddAdapter(getBaseContext(), R.layout.coin_add_list_view, initCoin(), coinPojos);
                Message msg = new Message();
                msg.obj = adapter;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private List<CoinPojo> initCoin() {
        String result = SharedPreferencesUtils.getString("ethCacheWallet", "coinList");
        if (null == result || result.trim().equals("")) {
            List<CoinPojo> list = new ArrayList();
      /* CoinPojo coin = new CoinPojo();
        coin.setCoinAddress("0xB364dc7d097612faCe58B4860E982a7a99f4f392");
        coin.setCoinName("XDLR");
        coin.setCoinSymbolName("LR");
        coin.setWalletId(ethCacheWallet.getId());
        list.add(coin);

        CoinPojo coin1 = new CoinPojo();
        coin1.setCoinAddress("0x21cfece802d90d137aca56600efc26daca85c7b2");
        coin1.setCoinSymbolName("QS");
        coin1.setCoinName("QS");
        coin1.setWalletId(ethCacheWallet.getId());
        list.add(coin1);*/
            CoinPojo coin = new CoinPojo();
            coin.setCoinAddress("1");
            coin.setCoinName("Augur Reputation");
            coin.setCoinSymbolName("REP");
            coin.setWalletId(ethCacheWallet.getId());
            list.add(coin);

            CoinPojo coin1 = new CoinPojo();
            coin1.setCoinAddress("2");
            coin1.setCoinName("Maker Dao");
            coin1.setCoinSymbolName("MKR");
            coin1.setWalletId(ethCacheWallet.getId());
            list.add(coin1);

            CoinPojo coin2 = new CoinPojo();
            coin2.setCoinAddress("3");
            coin2.setCoinName("Golem NetWork Token");
            coin2.setCoinSymbolName("GNT");
            coin2.setWalletId(ethCacheWallet.getId());
            list.add(coin2);

            CoinPojo coin3 = new CoinPojo();
            coin3.setCoinAddress("4");
            coin3.setCoinName("FirstBlood Token");
            coin3.setCoinSymbolName("1ST");
            coin3.setWalletId(ethCacheWallet.getId());
            list.add(coin3);

            CoinPojo coin4 = new CoinPojo();
            coin4.setCoinAddress("0x6f6eef16939b8327d53afdcaf08a72bba99c1a7f");
            coin4.setCoinName("KANGB INTL");
            coin4.setCoinSymbolName("KBI");
            coin4.setWalletId(ethCacheWallet.getId());
            list.add(coin4);

            SharedPreferencesUtils.writeString("ethCacheWallet", "coinList", JsonUtils.objectToJson(list));
            return list;
        } else {
            List<CoinPojo> coinPojos = JsonUtils.jsonToList(result, CoinPojo.class);
            return coinPojos;
        }
    }

    private void addHeader() {
        LayoutInflater inflater = getLayoutInflater();
        View headView = inflater.inflate(R.layout.coin_add_list_view, null);

        ImageView icon = (ImageView) headView.findViewById(R.id.CoinAddIcon);
        TextView coinName = (TextView) headView.findViewById(R.id.coinSymbolName);
        TextView coinLongName = (TextView) headView.findViewById(R.id.coinAddName);
        Switch swith = (Switch) headView.findViewById(R.id.addCoinSwitch);

        swith.setVisibility(View.INVISIBLE);
        icon.setImageResource(R.drawable.coin_eth);
        coinName.setText("ETH");
        coinLongName.setText("Ethereum Foundation");

        listView.addHeaderView(headView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addressInfoPreBut:
                CoinAddActivity.this.finish();
                break;
            case R.id.coinAddSousuo:
                Intent intent = new Intent(CoinAddActivity.this, CoinAddByContractActivity.class);
                intent.putExtra("CoinPojos", JsonUtils.objectToJson(coinPojos));
                startActivity(intent);
                CoinAddActivity.this.finish();
                break;
        }
    }
}
