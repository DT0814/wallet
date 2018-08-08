package lr.com.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import lr.com.wallet.R;
import lr.com.wallet.dao.TxCacheDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.pojo.TxCacheBean;
import lr.com.wallet.pojo.TxPojo;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.TxUtils;


@SuppressLint("NewApi")
public class CoinInfoActivity extends FragmentActivity implements View.OnClickListener {

    private ImageButton addressInfoPreBut;
    private TextView infoWalletName;
    private TextView infoCoinNum;
    private LinearLayout sendTransaction;
    private CoinPojo coin;
    private Timer timer = new Timer();
    private LinearLayout incomeBut;
    private LineChart mChart;
    private LineData data;
    private ETHWallet ethWallet;

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
        mChart = (LineChart) findViewById(R.id.lineChart);

        initmChart();


        initFrame();
    }

    private void initFrame() {
        TxCacheBean txCache = TxCacheDao.getTxCache(ethWallet.getId().toString(), coin.getCoinId().toString());
        if (null == txCache || null == txCache.getData()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coinInfoFrame, new CoinInfoNoTxlistFragment()).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coinInfoFrame, new CoinInfoTxlistFragment(coin)).commitAllowingStateLoss();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (coin.getCoinAddress().length() < 10) {
                    return;
                }
                TxPojo txPojo = getTxPojo();
                if (null != txPojo && txPojo.getResult().size() > 0) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.coinInfoFrame, new CoinInfoTxlistFragment(coin)).commitAllowingStateLoss();
                }
            }
        }).start();

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
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String s = value + "余额";
                return s;
            }
        });
        //去除右边的y轴
      /*  YAxis yAxisRight = mChart.getAxisRight();
        yAxisRight.setEnabled(false);*/
        YAxis axisLeft = mChart.getAxisLeft();
        axisLeft.setEnabled(false);
        YAxis axisRight = mChart.getAxisRight();
        axisRight.setDrawGridLines(false);
        axisRight.setLabelCount(2);
        axisRight.setDrawLabels(false);
        init();
    }

    private void init() {
        //初始化数据
        String xl[] = {"1", "2", "3", "4", "5", "6", "7"}; //横轴数据
        String yl[] = {"0", "0", "0", "0", "0", "0", "10"}; //竖轴数据
        data = getData(xl, yl);
        mChart.setData(data);
        mChart.animateX(0);//动画时间
    }

    private LineData getData(String[] xx, String[] yy) {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < yy.length; i++) {
            yVals.add(new Entry(Float.parseFloat(xx[i]), Float.parseFloat(yy[i])));
        }
        LineDataSet set = new LineDataSet(yVals, "");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置曲线为圆滑的线
        set.setCubicIntensity(0.1f);
        set.setDrawCircles(false);  //设置有圆点
        set.setLineWidth(1f);    //设置线的宽度
        set.setDrawFilled(true);//设置包括的范围区域填充颜色
        set.setCircleColor(getResources().getColor(R.color.colorPrimary, null));
        set.setColor(getResources().getColor(R.color.colorPrimary, null));
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set); // add the datasets
        LineData data = new LineData(dataSets);
        return data;
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
