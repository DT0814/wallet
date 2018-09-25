package lr.com.wallet.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lr.com.wallet.R;
import lr.com.wallet.adapter.TxAdapter;
import lr.com.wallet.adapter.TxListView;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.dao.TxCacheDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.pojo.TxCacheBean;
import lr.com.wallet.pojo.TxPojo;
import lr.com.wallet.pojo.TxStatusBean;
import lr.com.wallet.pojo.TxStatusResult;
import lr.com.wallet.utils.DateUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.TxComparator;
import lr.com.wallet.utils.TxStatusUtils;
import lr.com.wallet.utils.TxUtils;
import lr.com.wallet.utils.UnfinishedTxPool;


@SuppressLint("NewApi")
public class CoinInfoActivity extends FragmentActivity implements View.OnClickListener, TxListView.IRefreshListener, TxListView.ILoadMoreListener {

    private CoinPojo coin;
    private Timer timer = new Timer();
    private LineChart mChart;
    private ETHCacheWallet ethCacheWallet;
    private TxListView listView;
    private Handler txListViewRefreHandler;
    private TxPojo txPojo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_info_layout);
        ethCacheWallet = CacheWalletDao.getCurrentWallet();
        findViewById(R.id.coinInfoPreBut).setOnClickListener(this);
        TextView infoWalletName = this.findViewById(R.id.infoWalletName);
        TextView infoCoinNum = this.findViewById(R.id.infoCoinNum);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("obj");
        coin = JsonUtils.jsonToPojo(stringExtra, CoinPojo.class);
        assert coin != null;
        infoCoinNum.setText(coin.getCoinCount());
        infoWalletName.setText(coin.getCoinSymbolName());
        LinearLayout sendTransaction = this.findViewById(R.id.sendTransaction);
        sendTransaction.setOnClickListener(this);
        LinearLayout incomeBut = findViewById(R.id.incomeBut);
        incomeBut.setOnClickListener(this);
        mChart = (LineChart) findViewById(R.id.lineChart);
        initmChart();
        initFrame();
    }

    private void initFrame() {
        TxCacheBean txCache = TxCacheDao.getTxCache(ethCacheWallet.getId().toString(), coin.getCoinId().toString());
        if (null == txCache || null == txCache.getData()) {
            initTransactionListView();
        } else {
            findViewById(R.id.noTxData).setVisibility(View.GONE);
            initTransactionListView();
        }
    }

    @SuppressLint("HandlerLeak")
    private void initTransactionListView() {
        listView = findViewById(R.id.transcationList);
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
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    TxCacheBean txCache = TxCacheDao.getTxCache(ethCacheWallet.getId().toString(), coin.getCoinId().toString());
                    if (null == txCache || null == txCache.getData()) {
                        TxPojo pojo = getTxPojo();
                        assert pojo != null;
                        List<TxBean> result = pojo.getResult();

                        txCache = new TxCacheBean(coin.getCoinId(), ethCacheWallet.getId(), result);
                        TxCacheDao.addTxCache(txCache);
                    } else {
                        Log.d("coinInfo", "缓存命中");
                    }

                    timer.schedule(new TimerTask() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            TxCacheBean txCache = TxCacheDao.getTxCache(ethCacheWallet.getId().toString()
                                    , coin.getCoinId().toString());
                            assert txCache != null;
                            int num = txCache.getNum();
                            TxPojo pojo = getTxPojo();
                            if (null == pojo || null == pojo.getResult()) {
                                return;
                            }
                            List<TxBean> data = pojo.getResult();
                            if (data.size() > num) {
                                updateListView(getData(txCache, data));
                            }
                        }
                    }, 0, 5000);

                    List<TxBean> data = txCache.getData();
                    data.forEach(System.out::println);
                    updateListView(data);
                    data = getData(txCache, data);
                    updateListView(data);
                    if (null != data || data.size() > 0) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listView.setVisibility(View.VISIBLE);
                                findViewById(R.id.noTxData).setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateListView(List<TxBean> data) {
        TxAdapter adapter = new TxAdapter(CoinInfoActivity.this
                , R.layout.tx_list_view_item
                , data
                , ethCacheWallet, coin);
        Message msg = new Message();
        msg.obj = adapter;
        txListViewRefreHandler.sendMessage(msg);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<TxBean> getData(TxCacheBean txCache, List<TxBean> data) {
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
            TxPojo pojo = new TxPojo();
            if (null != txPojo) {
                pojo.setMessage(txPojo.getMessage());
                pojo.setResult(txPojo.getResult());
                pojo.setStatus(txPojo.getStatus());
                txPojo = null;
                return pojo;
            }
            if (!coin.getCoinSymbolName().equalsIgnoreCase("eth")) {
                pojo = TxUtils.getTransactionPojoByAddressAndContractAddress(
                        ethCacheWallet.getAddress(), coin.getCoinAddress());
            } else {
                pojo = TxUtils.getTransactionPojoByAddress(ethCacheWallet.getAddress());
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    TxCacheBean txCache = TxCacheDao.getTxCache(ethCacheWallet.getId().toString()
                            , coin.getCoinId().toString());
                    TxPojo pojo = getTxPojo();
                    assert pojo != null;
                    List<TxBean> data = pojo.getResult();
                    updateListView(getData(txCache, data));
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

    //折线图横轴自定义显示数据
    String x1String[];

    private void initmChart() {
        mChart.setDrawGridBackground(false);
        // 无描述文本
        mChart.getDescription().setEnabled(false);
        // 使能拖动和缩放
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        // 如果为false，则x，y两个方向可分别缩放
        mChart.setPinchZoom(true);
        // 没有数据的时候，显示“暂无数据”
        mChart.setNoDataText("暂无数据");
        //去掉LineSet标签
        Legend legend = mChart.getLegend();
        legend.setEnabled(false);
        //设置x轴位置
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(3);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return x1String[(int) value - 1];
            }
        });
        YAxis axisLeft = mChart.getAxisLeft();
        axisLeft.setEnabled(false);
        YAxis axisRight = mChart.getAxisRight();
        axisRight.setDrawGridLines(false);
        axisRight.setLabelCount(3);
        // axisRight.setDrawLabels(false);
        init();
    }

    private void init() {
        //初始化数据

        String xl[] = {"1", "2", "3", "4", "5"}; //横轴数据
        x1String = DateUtils.getLineDataXData(xl.length);
        String yl[] = {"0", "0", "0", "0", "10"}; //竖轴数据

        if (coin.getCoinSymbolName().equalsIgnoreCase("ETH")) {
            yl[yl.length - 1] = ethCacheWallet.getBalance();
        } else {
            yl[yl.length - 1] = coin.getCoinCount();
        }

        LineData data = getData(xl, yl);
        mChart.setData(data);
        mChart.animateX(0);//动画时间
    }

    private LineData getData(String[] xx, String[] yy) {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < yy.length; i++) {
            yVals.add(new Entry(Float.parseFloat(xx[i]), Float.parseFloat(yy[i])));
        }
        LineDataSet set = new LineDataSet(yVals, "");
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置曲线为圆滑的线
        set.setCubicIntensity(0.1f);
        set.setDrawCircles(false);  //设置有圆点
        set.setLineWidth(1f);    //设置线的宽度
        set.setDrawFilled(true);//设置包括的范围区域填充颜色
        set.setCircleColor(getResources().getColor(R.color.text_color_blue, null));
        set.setColor(getResources().getColor(R.color.colorPrimary, null));
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set); // add the datasets
        LineData data = new LineData(dataSets);
        return data;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coinInfoPreBut:
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
