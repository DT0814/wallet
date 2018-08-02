package lr.com.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lr.com.wallet.R;
import lr.com.wallet.adapter.CoinAdapter;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.TxCacheDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.pojo.TxCacheBean;
import lr.com.wallet.pojo.TxStatusBean;
import lr.com.wallet.pojo.TxStatusResult;
import lr.com.wallet.utils.CoinUtils;
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
                                            System.out.println(v.getHash() + "hash:::::" + coinPojo.getCoinId());
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
                        String balanceOf = CoinUtils.getBalanceOf(coinPojo.getCoinAddress(), ethWallet.getAddress());
                        if (!balanceOf.equalsIgnoreCase(coinPojo.getCoinAddress())) {
                            coinPojo.setCoinCount(balanceOf);
                            CoinDao.updateCoinPojo(coinPojo);
                        }
                    } else {
                        try {
                            String balance = Web3jUtil.ethGetBalance(ethWallet.getAddress());
                            if (!coinPojo.getCoinCount().equalsIgnoreCase(balance)) {
                                coinPojo.setCoinCount(balance);
                                CoinDao.updateCoinPojo(coinPojo);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                String s = null;
                try {
                    s = Web3jUtil.ethGetBalance(ethWallet.getAddress());
                    ethWallet.setNum(new BigDecimal(s));
                    WalletDao.writeCurrentJsonWallet(ethWallet);
                    Message ms = new Message();
                    if (s.indexOf(".") != -1 && s.indexOf(".") + 5 < s.length()) {
                        ms.obj = "ETH: " + s.substring(0, s.indexOf(".") + 5);
                    } else {
                        ms.obj = "ETH: " + s;
                    }
                    ethNumHandler.sendMessage(ms);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updataCoinListView(coinPojos);
            }
        }, 1000, 5000);
    }


    Handler ethNumHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String model = (String) msg.obj;
            ethNum.setText(model);
        }
    };

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


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = Web3jUtil.ethGetBalance(ethWallet.getAddress());
                    Message ms = new Message();
                    if (s.indexOf(".") != -1 && s.indexOf(".") + 5 < s.length()) {
                        ms.obj = "ETH: " + s.substring(0, s.indexOf(".") + 5);
                    } else {
                        ms.obj = "ETH: " + s;
                    }
                    ethNumHandler.sendMessage(ms);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return view;
    }


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
        CoinAdapter adapter = new CoinAdapter(activity, R.layout.coin_list_view, coinPojos);
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
}
