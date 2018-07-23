package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.bitcoinj.core.Coin;

import lr.com.wallet.R;
import lr.com.wallet.adapter.TransactionAdapter;
import lr.com.wallet.adapter.TxListView;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TransactionBean;
import lr.com.wallet.pojo.TransactionPojo;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;
import lr.com.wallet.utils.TransactionUtils;

/**
 * Created by dt0814 on 2018/7/22.
 */

public class CoinInfoActivity extends Activity implements View.OnClickListener, TxListView.IRefreshListener, TxListView.ILoadMoreListener {
    private TxListView listView;
    private Handler txListViewRefreHandler;
    private ETHWallet ethWallet;
    private ImageButton addressInfoPreBut;
    private TextView infoWalletName;
    private TextView infoCoinNum;
    private Button sendTransaction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_info_layout);
        ethWallet = WalletDao.getCurrentWallet();
        addressInfoPreBut = findViewById(R.id.addressInfoPreBut);
        addressInfoPreBut.setOnClickListener(this);
        infoWalletName = this.findViewById(R.id.infoWalletName);
        infoCoinNum = this.findViewById(R.id.infoCoinNum);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("obj");
        CoinPojo coin = JsonUtils.jsonToPojo(stringExtra, CoinPojo.class);
        infoCoinNum.setText(coin.getCoinCount());
        infoWalletName.setText(coin.getCoinSymbolName());
        sendTransaction = this.findViewById(R.id.sendTransaction);
        sendTransaction.setOnClickListener(this);

        initTransactionListView();
    }

    private void initTransactionListView() {
        listView = (TxListView) this.findViewById(R.id.transcationList);
        listView.setIRefreshListener(this);
        listView.setILoadMoreListener(this);
        txListViewRefreHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter((ListAdapter) msg.obj);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TransactionBean itemAtPosition = (TransactionBean) adapterView.getItemAtPosition(i);
                        Intent intent = new Intent(CoinInfoActivity.this, TxInfoActivity.class);
                        intent.putExtra("obj", JsonUtils.objectToJson(itemAtPosition));
                        startActivity(intent);
                    }
                });
                listView.refreshComplete();
                //  listView.loadMoreComplete();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionPojo pojo;
                    pojo = TransactionUtils.getTransactionPojo(ethWallet.getAddress());
                    TransactionAdapter adapter = new TransactionAdapter(CoinInfoActivity.this, R.layout.tx_list_view, pojo.getResult(), ethWallet);
                    Message msg = new Message();
                    msg.obj = adapter;
                    txListViewRefreHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void setRefreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionPojo pojo;
                    pojo = TransactionUtils.getTransactionPojo(ethWallet.getAddress());
                    TransactionAdapter adapter = new TransactionAdapter(CoinInfoActivity.this, R.layout.tx_list_view, pojo.getResult(), ethWallet);
                    Message msg = new Message();
                    msg.obj = adapter;
                    txListViewRefreHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void setLoadData() {


    }

    @Override
    public void onRefresh() {
        //获取最新数据
        setRefreshData();
    }

    @Override
    public void onLoadMore() {
        //获取最新数据
        setLoadData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addressInfoPreBut:
                CoinInfoActivity.this.finish();
                break;
            case R.id.sendTransaction:
                startActivity(new Intent(CoinInfoActivity.this, TxActivity.class));
                break;
        }
    }
}
