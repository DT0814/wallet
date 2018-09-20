package lr.com.wallet.activity.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import lr.com.wallet.activity.AddressShowActivity;
import lr.com.wallet.activity.CoinAddActivity;
import lr.com.wallet.activity.CoinInfoActivity;
import lr.com.wallet.activity.CreateWalletActivity;
import lr.com.wallet.adapter.CoinAdapter;
import lr.com.wallet.adapter.WalletSymAdapter;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.TxCacheDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.pojo.ETHPriceResult;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.pojo.TxCacheBean;
import lr.com.wallet.pojo.TxStatusBean;
import lr.com.wallet.pojo.TxStatusResult;
import lr.com.wallet.utils.CoinUtils;
import lr.com.wallet.utils.CustomDrawerLayout;
import lr.com.wallet.utils.DateUtils;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.HTTPUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.TxStatusUtils;
import lr.com.wallet.utils.UnfinishedTxPool;
import lr.com.wallet.utils.Web3jUtil;

@SuppressLint("HandlerLeak")
public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity activity;
    private Activity baseActivity;
    private ETHCacheWallet ethCacheWallet;
    private TextView ethNum;
    private View view;
    private ListView coinListView;
    private Handler coinListViewHandler;
    private List<CoinPojo> coinPojos;
    private Timer timer;
    private TextView walletName;
    private CustomDrawerLayout drawerLayout; //侧边导航栏
    private ImageView touxiang;

    public HomeFragment() {

    }

    @SuppressLint("ValidFragment")
    public HomeFragment(Activity baseActivity) {
        this();
        this.baseActivity = baseActivity;
        Log.i("父布局", baseActivity.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_home_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();

        initDrawerLayout();

        ethNum = view.findViewById(R.id.ethNum);
        ethCacheWallet = CacheWalletDao.getCurrentWallet();
        touxiang = view.findViewById(R.id.touxiang);
        touxiang.setOnClickListener(this);
        ETHWalletUtils.switchTouXiangImg(touxiang, ethCacheWallet.getTongxingID());

        if (null == ethCacheWallet) {
            startActivity(new Intent(activity, CreateWalletActivity.class));
            return null;
        }
        View toAddressLayout = view.findViewById(R.id.toAddressLayout);
        toAddressLayout.setOnClickListener(this);
        ImageButton addCoinBut = view.findViewById(R.id.addCoinBut);
        addCoinBut.setOnClickListener(this);
        walletName = view.findViewById(R.id.walletName);
        walletName.setText(ethCacheWallet.getName());
        walletName.setOnClickListener(this);
        TextView homeShowAddress = view.findViewById(R.id.homeShowAddress);
        homeShowAddress.setText(ethCacheWallet.getAddress());
        ethNum.setText(ethCacheWallet.getBalance());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = null;
                try {
                    s = Web3jUtil.ethGetBalance(ethCacheWallet.getAddress());
                    ethCacheWallet.setNum(new BigDecimal(s));
                    CacheWalletDao.writeCurrentJsonWallet(ethCacheWallet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ImageButton mainMenuBut = view.findViewById(R.id.mainMenuBtn);
        mainMenuBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.END);//打开侧边导航栏
            }
        });
        getActivity().findViewById(R.id.bgLayout).setBackgroundResource(R.drawable.zichanbg);
        return view;
    }

    private void initDrawerLayout() {
        drawerLayout = view.findViewById(R.id.home_drawer_layout); //侧边导航栏
        NavigationView navigationView = view.findViewById(R.id.nav_view);
        View nView = navigationView.getHeaderView(0);
        RecyclerView recyclerViewWallet = nView.findViewById(R.id.listView);
        StaggeredGridLayoutManager layoutManager2 = new
                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewWallet.setLayoutManager(layoutManager2);
        recyclerViewWallet.setNestedScrollingEnabled(false);
        List<ETHCacheWallet> allWallet = CacheWalletDao.getAllWallet();
        WalletSymAdapter adapter = new WalletSymAdapter(allWallet);
        recyclerViewWallet.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
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
        initCacheWallet();
        initCoinListView();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("定时调度", "定时调度" + DateUtils.getDateFormatByString(new Date().getTime()));
                coinPojos = CoinDao.getConinListByWalletId(ethCacheWallet.getId());
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
                                                TxCacheBean txCache = TxCacheDao.getTxCache(ethCacheWallet.getId().toString()
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
                            continue;
                        }
                        String balanceOf = CoinUtils.getBalanceOf(coinPojo.getCoinAddress(), ethCacheWallet.getAddress());
                        if (!balanceOf.equalsIgnoreCase(coinPojo.getCoinAddress())) {
                            coinPojo.setCoinCount(balanceOf);
                            CoinDao.updateCoinPojo(coinPojo);
                        }
                    } else {
                        try {
                            String s = Web3jUtil.ethGetBalance(ethCacheWallet.getAddress());

/*
                            ETHPriceResult price = HTTPUtils.ETHPriceResult(
                                    "http://fxhapi.feixiaohao.com/public/v1/ticker?code=ethereum&convert=CNY"
                                    , ETHPriceResult.class);
*/

                            List<ETHPriceResult> list = HTTPUtils.getList(
                                    "http://fxhapi.feixiaohao.com/public/v1/ticker?code=ethereum&convert=CNY"
                                    , ETHPriceResult.class);
                            if (null == list || list.size() < 1) {
                                return;
                            }
                            ETHPriceResult price = list.get(0);

                            if (null == price || null == s || s.equals("")) {
                                return;
                            }
                            String ethusd = price.getPrice_cny();
                            BigDecimal dollar = new BigDecimal(ethusd);
                            BigDecimal ethNum = new BigDecimal(s);
                            BigDecimal balance = dollar.multiply(ethNum);
                            String balanceStr = balance.toString();
                            if (balanceStr.indexOf(".") != -1 && balanceStr.indexOf(".") + 5 < balanceStr.length()) {
                                balanceStr = balanceStr.substring(0, balanceStr.indexOf(".") + 5);
                            }
                            if (s.indexOf(".") != -1 && s.indexOf(".") + 5 < s.length()) {
                                s = s.substring(0, s.indexOf(".") + 5);
                            }

                            if (!coinPojo.getCoinCount().equalsIgnoreCase(s)) {
                                coinPojo.setCoinCount(s);
                                CoinDao.updateCoinPojo(coinPojo);
                            }
                            Message ms = new Message();
                            ms.obj = balanceStr;
                            ethNumHandler.sendMessage(ms);
                            ethCacheWallet.setBalance(balanceStr);
                            ethCacheWallet.setNum(ethNum);
                            CacheWalletDao.writeCurrentJsonWallet(ethCacheWallet);
                            CacheWalletDao.writeJsonWallet(ethCacheWallet);

                            coinPojo.setCoinBalance(balanceStr);
                            CoinDao.updateCoinPojo(coinPojo);
                            updataCoinListView(coinPojos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                updataCoinListView(coinPojos);
            }
        }, 0, 20000);
    }

    private void initCacheWallet() {
        ethCacheWallet = CacheWalletDao.getCurrentWallet();
        ETHWalletUtils.switchTouXiangImg(touxiang, ethCacheWallet.getTongxingID());
        walletName.setText(ethCacheWallet.getName());
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
        coinPojos = CoinDao.getConinListByWalletId(ethCacheWallet.getId());
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
            case R.id.touxiang:
                startActivity(new Intent(activity, AddressShowActivity.class));
                break;
            case R.id.walletName:
                startActivity(new Intent(activity, AddressShowActivity.class));
                break;
        }
    }


}
