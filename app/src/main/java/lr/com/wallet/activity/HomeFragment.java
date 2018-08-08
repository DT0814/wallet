package lr.com.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lr.com.wallet.R;
import lr.com.wallet.adapter.CoinAdapter;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.TxCacheDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHPrice;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.ExchangeRate;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.pojo.TxCacheBean;
import lr.com.wallet.pojo.TxStatusBean;
import lr.com.wallet.pojo.TxStatusResult;
import lr.com.wallet.utils.CoinUtils;
import lr.com.wallet.utils.DateUtils;
import lr.com.wallet.utils.HTTPUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.TxStatusUtils;
import lr.com.wallet.utils.UnfinishedTxPool;
import lr.com.wallet.utils.Web3jUtil;

@SuppressLint("NewApi")
public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity activity;
    private ETHWallet ethWallet;
    private TextView ethNum;
    private View view;
    private ListView coinListView;
    private Handler coinListViewHandler;
    private ImageButton addCoinBut;
    private View toAddressLayout;
    private TextView walletName;
    private TextView homeShowAddress;
    private List<CoinPojo> coinPojos;
    private Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, null);
        super.onCreate(savedInstanceState);
        ethNum = view.findViewById(R.id.ethNum);
        activity = getActivity();
        ethWallet = WalletDao.getCurrentWallet();
        if (null == ethWallet) {
            startActivity(new Intent(activity, CreateWalletActivity.class));
            return null;
        }
        toAddressLayout = view.findViewById(R.id.toAddressLayout);
        toAddressLayout.setOnClickListener(this);
        addCoinBut = view.findViewById(R.id.addCoinBut);
        addCoinBut.setOnClickListener(this);
        walletName = view.findViewById(R.id.walletName);
        walletName.setText(ethWallet.getName());
        homeShowAddress = view.findViewById(R.id.homeShowAddress);
        homeShowAddress.setText(ethWallet.getAddress());
        ethNum.setText(ethWallet.getBalance());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = null;
                try {
                    s = Web3jUtil.ethGetBalance(ethWallet.getAddress());
                    ethWallet.setNum(new BigDecimal(s));
                    WalletDao.writeCurrentJsonWallet(ethWallet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        Log.i("手机像素", scale + "");
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause______HomeFragment");
        timer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume______HomeFragment");
        initCoinListView();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("定时调度", "定时调度" + DateUtils.getDateFormatByString(new Date().getTime()));
                coinPojos = CoinDao.getConinListByWalletId(ethWallet.getId());
                for (CoinPojo coinPojo : coinPojos) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<TxBean> unfinishedTxByCoinid =
                                    UnfinishedTxPool.getUnfinishedTxByCoinid(coinPojo.getCoinId().toString());
                            unfinishedTxByCoinid.forEach((v) -> {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            TxStatusBean txStatusByHash = TxStatusUtils.getTxStatusByHash(v.getHash());
                                            TxStatusResult result = txStatusByHash.getResult();
                                            String status = result.getStatus();
                                            if (status.equals("1")) {
                                                UnfinishedTxPool.deleteUnfinishedTx(v, coinPojo.getCoinId().toString());
                                            } else if (status.equals("0")) {
                                                TxCacheBean txCache = TxCacheDao.getTxCache(ethWallet.getId().toString()
                                                        , coinPojo.getCoinId().toString());
                                                List<TxBean> data = txCache.getErrData();
                                                v.setStatus("0");
                                                data.add(v);
                                                UnfinishedTxPool.deleteUnfinishedTx(v, coinPojo.getCoinId().toString());
                                                //添加覆盖 相当于更新
                                                TxCacheDao.addTxCache(txCache);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            });
                        }
                    }).start();

                    //不是以太币
                    if (!coinPojo.getCoinSymbolName().equalsIgnoreCase("ETH")) {
                        if (coinPojo.getCoinAddress().length() < 10) {
                            return;
                        }
                        String balanceOf = CoinUtils.getBalanceOf(coinPojo.getCoinAddress(), ethWallet.getAddress());
                        if (!balanceOf.equalsIgnoreCase(coinPojo.getCoinAddress())) {
                            coinPojo.setCoinCount(balanceOf);
                            CoinDao.updateCoinPojo(coinPojo);
                        }
                    } else {
                        try {
                            String s = Web3jUtil.ethGetBalance(ethWallet.getAddress());
                            if (!coinPojo.getCoinCount().equalsIgnoreCase(s)) {
                                coinPojo.setCoinCount(s);
                                CoinDao.updateCoinPojo(coinPojo);
                            }
                            ETHPrice price = HTTPUtils.getUtils(
                                    "https://api.etherscan.io/api?module=stats&action=ethprice&apikey=c0oGHqQQlq6XJU2kz5DL"
                                    , ETHPrice.class);

                            if (null == price || null == s || s.equals("")
                                    || s.equals("0") || !price.getStatus().equals("1")) {
                                return;
                            }
                            String ethusd = price.getResult().getEthusd();
                            BigDecimal dollar = new BigDecimal(ethusd);
                            BigDecimal ethNum = new BigDecimal(s);
                            BigDecimal balance = dollar.multiply(ethNum);
                            String balanceStr = balance.toString();
                            if (balanceStr.indexOf(".") != -1 && s.indexOf(".") + 5 < s.length()) {
                                balanceStr = balanceStr.substring(0, balanceStr.indexOf(".") + 5);
                            }
                            coinPojo.setCoinBalance(balanceStr);
                            CoinDao.updateCoinPojo(coinPojo);
                            updataCoinListView(coinPojos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    String s = Web3jUtil.ethGetBalance(ethWallet.getAddress());
                    ethWallet.setNum(new BigDecimal(s));
                    WalletDao.writeCurrentJsonWallet(ethWallet);
                    WalletDao.writeJsonWallet(ethWallet);
                    ETHPrice price = HTTPUtils.getUtils(
                            "https://api.etherscan.io/api?module=stats&action=ethprice&apikey=c0oGHqQQlq6XJU2kz5DL"
                            , ETHPrice.class);

                    if (null == price || null == s || s.equals("") || s.equals("0") || !price.getStatus().equals("1")) {
                        return;
                    }
                    String ethusd = price.getResult().getEthusd();
                    BigDecimal dollar = new BigDecimal(ethusd);
                    BigDecimal ethNum = new BigDecimal(s);
                    BigDecimal balance = dollar.multiply(ethNum);
                    String balanceStr = balance.toString();
                /*    ExchangeRate rate = HTTPUtils.getUtils(
                            "http://api.jisuapi.com/exchange/convert?appkey=04a48bfc6846659d&from=USD&to=CNY&amount=" + balanceStr
                            , ExchangeRate.class);
                    if (null == rate || !rate.getStatus().equals("0")) {
                        return;
                    }
                    balanceStr = rate.getResult().getCamount();*/
                    Message ms = new Message();
                    if (balanceStr.indexOf(".") != -1 && s.indexOf(".") + 5 < s.length()) {
                        balanceStr = balanceStr.substring(0, balanceStr.indexOf(".") + 5);
                        ms.obj = balanceStr;
                    } else {
                        ms.obj = balanceStr;
                    }
                    if (!ethWallet.getBalance().equalsIgnoreCase(balanceStr)) {
                        ethNumHandler.sendMessage(ms);
                        ethWallet.setBalance(balanceStr);
                        WalletDao.writeCurrentJsonWallet(ethWallet);
                        WalletDao.writeJsonWallet(ethWallet);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updataCoinListView(coinPojos);
            }
        }, 0, 20000);
    }


    Handler ethNumHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String model = (String) msg.obj;
            ethNum.setText(model);
        }
    };


    private void initCoinListView() {
        coinListView = (ListView) view.findViewById(R.id.coinListView);
        coinListViewHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ListAdapter obj = (ListAdapter) msg.obj;
                coinListView.setAdapter(obj);
                coinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CoinPojo itemAtPosition = (CoinPojo) adapterView.getItemAtPosition(i);
                        Intent intent = new Intent(activity, CoinInfoActivity.class);
                        intent.putExtra("obj", JsonUtils.objectToJson(itemAtPosition));
                        startActivity(intent);
                    }
                });
            }

        };
        coinPojos = CoinDao.getConinListByWalletId(ethWallet.getId());
        updataCoinListView(coinPojos);
    }

    private void updataCoinListView(List<CoinPojo> coinPojos) {
        List<CoinPojo> list = new ArrayList(coinPojos);
        list.sort(new Comparator<CoinPojo>() {
            @Override
            public int compare(CoinPojo coinPojo, CoinPojo t1) {
                return coinPojo.getCoinId().intValue() - t1.getCoinId().intValue();
            }
        });
        CoinAdapter adapter = new CoinAdapter(activity, R.layout.coin_list_view, list);
        Message msg = new Message();
        msg.obj = adapter;
        coinListViewHandler.sendMessage(msg);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addCoinBut:
                Intent intent = new Intent(activity, CoinAddActivity.class);
                intent.putExtra("CoinPojos", JsonUtils.objectToJson(coinPojos));
                startActivity(intent);
                break;
            case R.id.toAddressLayout:
                startActivity(new Intent(activity, AddressShowActivity.class));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
