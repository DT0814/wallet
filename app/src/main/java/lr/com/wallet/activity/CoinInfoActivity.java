package lr.com.wallet.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lr.com.wallet.R;
import lr.com.wallet.adapter.TxAdapter;
import lr.com.wallet.adapter.TxListView;
import lr.com.wallet.dao.TxCacheDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.pojo.TxCacheBean;
import lr.com.wallet.pojo.TxPojo;
import lr.com.wallet.pojo.TxStatusBean;
import lr.com.wallet.pojo.TxStatusResult;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.TxComparator;
import lr.com.wallet.utils.TxStatusUtils;
import lr.com.wallet.utils.TxUtils;
import lr.com.wallet.utils.UnfinishedTxPool;

@SuppressLint("NewApi")
public class CoinInfoActivity extends Activity implements View.OnClickListener, TxListView.IRefreshListener, TxListView.ILoadMoreListener {
    private TxListView listView;
    private Handler txListViewRefreHandler;
    private ETHWallet ethWallet;
    private ImageButton addressInfoPreBut;
    private TextView infoWalletName;
    private TextView infoCoinNum;
    private Button sendTransaction;
    private CoinPojo coin;
    private Timer timer = new Timer();
    private Button incomeBut;

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
        coin = JsonUtils.jsonToPojo(stringExtra, CoinPojo.class);
        assert coin != null;
        infoCoinNum.setText(coin.getCoinCount());
        infoWalletName.setText(coin.getCoinSymbolName());
        sendTransaction = this.findViewById(R.id.sendTransaction);
        sendTransaction.setOnClickListener(this);
        incomeBut = findViewById(R.id.incomeBut);
        incomeBut.setOnClickListener(this);
        //初始化交易
        initTransactionListView();
    }


    @SuppressLint("HandlerLeak")
    private void initTransactionListView() {
        listView = this.findViewById(R.id.transcationList);
        listView.setIRefreshListener(this);
        listView.setILoadMoreListener(this);
        txListViewRefreHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter((ListAdapter) msg.obj);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TxBean itemAtPosition = (TxBean) adapterView.getItemAtPosition(i);
                        Intent intent = new Intent(CoinInfoActivity.this, TxInfoActivity.class);
                        intent.putExtra("txBean", JsonUtils.objectToJson(itemAtPosition));
                        intent.putExtra("coin", JsonUtils.objectToJson(coin));
                        startActivity(intent);
                    }
                });
                listView.refreshComplete();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TxCacheBean txCache = TxCacheDao.getTxCache(ethWallet.getId().toString(), coin.getCoinId().toString());
                    if (null == txCache || null == txCache.getData()) {
                        TxPojo pojo = getTxPojo();
                        assert pojo != null;
                        List<TxBean> result = pojo.getResult();
                        txCache = new TxCacheBean(coin.getCoinId(), ethWallet.getId(), result);
                        TxCacheDao.addTxCache(txCache);

                    } else {
                        Log.d("coinInfo", "缓存命中");
                    }
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            TxCacheBean txCache = TxCacheDao.getTxCache(ethWallet.getId().toString()
                                    , coin.getCoinId().toString());
                            assert txCache != null;
                            int num = txCache.getNum();
                            TxPojo pojo = getTxPojo();
                            if (null == pojo || null == pojo.getResult()) {
                                return;
                            }
                            List<TxBean> data = pojo.getResult();
                            if (data.size() > num) {
                                updateListView(getDatas(txCache, data));
                            }
                        }
                    }, 2000, 3000);

                    List<TxBean> data = txCache.getData();
                    updateListView(data);

                    data = getDatas(txCache, data);
                    updateListView(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateListView(List<TxBean> data) {
        TxAdapter adapter = new TxAdapter(CoinInfoActivity.this
                , R.layout.tx_list_view
                , data
                , ethWallet, coin);
        Message msg = new Message();
        msg.obj = adapter;
        txListViewRefreHandler.sendMessage(msg);
    }

    private List<TxBean> getDatas(TxCacheBean txCache, List<TxBean> data) {
        txCache.setData(data);
        txCache.setNum(data.size());
        TxCacheDao.addTxCache(txCache);
        List<TxBean> unfinishedTxByCoinid = UnfinishedTxPool.getUnfinishedTxByCoinid(coin.getCoinId().toString());
        for (int i = 0; i < unfinishedTxByCoinid.size(); i++) {
            TxBean txBean = unfinishedTxByCoinid.get(i);
            try {
                TxStatusBean txStatusByHash = TxStatusUtils.getTxStatusByHash(txBean.getHash());
                assert txStatusByHash != null;
                TxStatusResult result = txStatusByHash.getResult();
                String status = result.getStatus();
                switch (status) {
                    case "1":
                        UnfinishedTxPool.deleteUnfinishedTx(txBean, coin.getCoinId().toString());
                        data.remove(txBean);
                        break;
                    case "0":
                        List<TxBean> txCacheData = txCache.getErrData();
                        txBean.setStatus("0");
                        txCacheData.add(txBean);
                        //添加覆盖 相当于更新
                        TxCacheDao.addTxCache(txCache);
                        UnfinishedTxPool.deleteUnfinishedTx(txBean, coin.getCoinId().toString());
                        data.remove(txBean);
                        break;
                    default:
                        data.add(i, txBean);
                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null != txCache.getErrData()) {
            data.addAll(txCache.getErrData());
        }
        data.sort(new TxComparator());
        return data;
    }

    private TxPojo getTxPojo() {
        try {
            TxPojo pojo;
            if (!coin.getCoinSymbolName().equalsIgnoreCase("eth")) {
                pojo = TxUtils.getTransactionPojoByAddressAndContractAddress(
                        ethWallet.getAddress(), coin.getCoinAddress());
            } else {
                pojo = TxUtils.getTransactionPojoByAddress(ethWallet.getAddress());
                assert pojo != null;
                List<TxBean> result = pojo.getResult();
                Iterator<TxBean> iterator = result.iterator();
                while (iterator.hasNext()) {
                    TxBean next = iterator.next();
                    if (next.getInput().length() > 2) {
                        iterator.remove();
                    }
                }
                pojo.setResult(result);
            }
            return pojo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setRefreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TxCacheBean txCache = TxCacheDao.getTxCache(ethWallet.getId().toString()
                            , coin.getCoinId().toString());
                    TxPojo pojo = getTxPojo();
                    assert pojo != null;
                    List<TxBean> data = pojo.getResult();
                    updateListView(getDatas(txCache, data));
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
                Intent intent = new Intent(CoinInfoActivity.this, TxActivity.class);
                intent.putExtra("coin", JsonUtils.objectToJson(coin));
                startActivity(intent);
                break;
            case R.id.incomeBut:
                startActivity(new Intent(CoinInfoActivity.this, AddressShowActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
