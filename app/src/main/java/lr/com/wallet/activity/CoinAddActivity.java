package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
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

public class CoinAddActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private ETHWallet ethWallet;
    private CoinPojo coin;
    private ImageButton addressInfoPreBut;
    private List<CoinPojo> coinPojos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_add_layout);
        listView = findViewById(R.id.addCoinListView);
        ethWallet = WalletDao.getCurrentWallet();
        addressInfoPreBut = findViewById(R.id.addressInfoPreBut);
        addressInfoPreBut.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String coinPojosJson = extras.getString("CoinPojos");
        if (null != coinPojosJson) {
            coinPojos = JsonUtils.jsonToList(coinPojosJson, CoinPojo.class);
        }
        Handler handler = new Handler() {
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
                List<CoinPojo> list = new ArrayList();
                coin = new CoinPojo();
                // coin.setCoinAddress("0x9d1fa651bf92043f26afdbca3a0548983d76ace5");
                //String name = CoinUtils.getName("0xB364dc7d097612faCe58B4860E982a7a99f4f392", ethWallet.getAddress());
                //String symbolName = CoinUtils.getSymbolName("0xB364dc7d097612faCe58B4860E982a7a99f4f392", ethWallet.getAddress());
                coin.setCoinAddress("0xB364dc7d097612faCe58B4860E982a7a99f4f392");
                coin.setCoinName("XDLR");
                coin.setCoinSymbolName("LR");
                coin.setWalletId(ethWallet.getId());
                list.add(coin);
                coin = new CoinPojo();
                coin.setCoinAddress("0x21cfece802d90d137aca56600efc26daca85c7b2");
                coin.setCoinSymbolName("QS");
                coin.setCoinName("QS");
                coin.setWalletId(ethWallet.getId());
                list.add(coin);

                CoinAddAdapter adapter = new CoinAddAdapter(getBaseContext(), R.layout.coin_add_list_view, list, coinPojos);
                Message msg = new Message();
                msg.obj = adapter;
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addressInfoPreBut:
                CoinAddActivity.this.finish();
                break;
        }
    }
}
