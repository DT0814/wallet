package lr.com.wallet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Coin;

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
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by dt0814 on 2018/7/23.
 */

public class CoinAddActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private ETHWallet ethWallet;
    private ImageButton addressInfoPreBut;
    private List<CoinPojo> coinPojos;
    private ImageButton coinAddSousuo;
    Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_add_layout);
        listView = findViewById(R.id.addCoinListView);
        addHeader();
        ethWallet = WalletDao.getCurrentWallet();
        addressInfoPreBut = findViewById(R.id.addressInfoPreBut);
        coinAddSousuo = findViewById(R.id.coinAddSousuo);
        coinAddSousuo.setOnClickListener(this);
        addressInfoPreBut.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String coinPojosJson = extras.getString("CoinPojos");
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
        String result = SharedPreferencesUtils.getString("ethWallet", "coinList");
        if (null == result || result.trim().equals("")) {
            List<CoinPojo> list = new ArrayList();
      /* CoinPojo coin = new CoinPojo();
        coin.setCoinAddress("0xB364dc7d097612faCe58B4860E982a7a99f4f392");
        coin.setCoinName("XDLR");
        coin.setCoinSymbolName("LR");
        coin.setWalletId(ethWallet.getId());
        list.add(coin);

        CoinPojo coin1 = new CoinPojo();
        coin1.setCoinAddress("0x21cfece802d90d137aca56600efc26daca85c7b2");
        coin1.setCoinSymbolName("QS");
        coin1.setCoinName("QS");
        coin1.setWalletId(ethWallet.getId());
        list.add(coin1);*/
            CoinPojo coin = new CoinPojo();
            coin.setCoinAddress("1");
            coin.setCoinName("Augur Reputation");
            coin.setCoinSymbolName("REP");
            coin.setWalletId(ethWallet.getId());
            list.add(coin);

            CoinPojo coin1 = new CoinPojo();
            coin1.setCoinAddress("2");
            coin1.setCoinName("Maker Dao");
            coin1.setCoinSymbolName("MKR");
            coin1.setWalletId(ethWallet.getId());
            list.add(coin1);

            CoinPojo coin2 = new CoinPojo();
            coin2.setCoinAddress("wallet_icon");
            coin2.setCoinName("Golem NetWork Token");
            coin2.setCoinSymbolName("GNT");
            coin2.setWalletId(ethWallet.getId());
            list.add(coin2);

            CoinPojo coin3 = new CoinPojo();
            coin3.setCoinAddress("4");
            coin3.setCoinName("FirstBlood Token");
            coin3.setCoinSymbolName("1ST");
            coin3.setWalletId(ethWallet.getId());
            list.add(coin3);
            SharedPreferencesUtils.writeString("ethWallet", "coinList", JsonUtils.objectToJson(list));
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
        icon.setImageResource(R.drawable.eth_coin);
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
                View showMnemonicLayout = getLayoutInflater().inflate(R.layout.input_coin_address_layout, null);
                EditText editText = showMnemonicLayout.findViewById(R.id.inCoinAddressBut);
                AlertDialog.Builder builder = new AlertDialog.Builder(CoinAddActivity.this);
                builder.setView(showMnemonicLayout);
                builder.setTitle("输入代币地址")
                        .setMessage("")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface z, int which) {
                                String address = editText.getText().toString();
                                if (null == address || address.trim().length() != 42) {
                                    Toast.makeText(CoinAddActivity.this, "代币地址为以0x开头的长度为42的字符串", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (CoinDao.CheckContains(address, ethWallet.getId())) {
                                    Toast.makeText(CoinAddActivity.this, "当前钱包已存在该代币请勿重复添加", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                CoinPojo coinPojo = CoinDao.addCoinPojo(address, ethWallet.getAddress());
                                if (null == coinPojo) {
                                    Toast.makeText(CoinAddActivity.this, "未查询到代币", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(CoinAddActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                                    String result = SharedPreferencesUtils.getString("ethWallet", "coinList");
                                    List<CoinPojo> coinPojos = JsonUtils.jsonToList(result, CoinPojo.class);
                                    coinPojos.add(coinPojo);
                                    SharedPreferencesUtils.writeString("ethWallet", "coinList", JsonUtils.objectToJson(coinPojos));
                                    CoinAddAdapter adapter = new CoinAddAdapter(getBaseContext(), R.layout.coin_add_list_view, initCoin(), coinPojos);
                                    Message msg = new Message();
                                    msg.obj = adapter;
                                    handler.sendMessage(msg);
                                    /*    Intent intent = new Intent(CoinAddActivity.this, MainFragmentActivity.class);
                                    startActivity(intent);*/
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                builder.show();
                break;
        }
    }
}
