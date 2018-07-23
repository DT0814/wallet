package lr.com.wallet.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.CoinAdapter;
import lr.com.wallet.adapter.TransactionAdapter;
import lr.com.wallet.adapter.TxListView;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TransactionBean;
import lr.com.wallet.pojo.TransactionPojo;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.TransactionUtils;
import lr.com.wallet.utils.Web3jUtil;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity activity;
    private Context context;
    private ETHWallet ethWallet;
    private LayoutInflater inflater;
    private AlertDialog.Builder alertbBuilder;
    private TextView ethNum;
    private View view;
    private ListView coinListView;
    private Handler coinListViewHandler;
    private ImageButton addCoinBut;
    private View toAddressLayout;
    private TextView walletName;
    private TextView homeShowAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.home_fragment, null);
        super.onCreate(savedInstanceState);
        ethNum = view.findViewById(R.id.ethNum);
        activity = getActivity();
        context = inflater.getContext();
        ethWallet = WalletDao.getCurrentWallet();
        if (null == ethWallet) {
            startActivity(new Intent(activity, CreateWalletActivity.class));
            return null;
        }

        toAddressLayout = view.findViewById(R.id.toAddressLayout);
        toAddressLayout.setOnClickListener(this);
        addCoinBut = view.findViewById(R.id.addCoinBut);
        addCoinBut.setOnClickListener(this);
        alertbBuilder = new AlertDialog.Builder(activity);
        walletName = view.findViewById(R.id.walletName);
        walletName.setText(ethWallet.getName());
        homeShowAddress = view.findViewById(R.id.homeShowAddress);
        homeShowAddress.setText(ethWallet.getAddress());
        Handler ethNumHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String model = (String) msg.obj;
                ethNum.setText(model);
            }
        };
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
        initCoinListView();
        return view;
    }

    private void initCoinListView() {
        coinListView = (ListView) view.findViewById(R.id.coinListView);
        coinListViewHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                coinListView.setAdapter((ListAdapter) msg.obj);
                coinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CoinPojo itemAtPosition = (CoinPojo) adapterView.getItemAtPosition(i);
                        Intent intent = new Intent(activity, CoinInfoActivity.class);
                        intent.putExtra("obj", JsonUtils.objectToJson(itemAtPosition));
                        System.out.println(itemAtPosition);
                        startActivity(intent);
                    }
                });
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<CoinPojo> coinPojos = CoinDao.getConinListByWalletId(ethWallet.getId());
                    CoinAdapter adapter = new CoinAdapter(activity, R.layout.coin_list_view, coinPojos);
                    Message msg = new Message();
                    msg.obj = adapter;
                    coinListViewHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addCoinBut:
                View inputCoinAddressView = inflater.inflate(R.layout.input_coin_address_layout, null);
                alertbBuilder.setView(inputCoinAddressView);
                alertbBuilder.setTitle("请输入Token合约地址").setMessage("").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = inputCoinAddressView.findViewById(R.id.inCoinAddressBut);
                                String address = editText.getText().toString();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CoinPojo coinPojo = CoinDao.addConinPojo(address);
                                        System.out.println(coinPojo);
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                List<CoinPojo> coinPojos = CoinDao.getConinListByWalletId(ethWallet.getId());
                                                CoinAdapter adapter = new CoinAdapter(activity, R.layout.coin_list_view, coinPojos);
                                                coinListView.setAdapter(adapter);
                                                coinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                        CoinPojo itemAtPosition = (CoinPojo) adapterView.getItemAtPosition(i);
                                                        Intent intent = new Intent(activity, CoinInfoActivity.class);
                                                        intent.putExtra("obj", JsonUtils.objectToJson(itemAtPosition));
                                                        System.out.println(itemAtPosition);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                }).create();
                alertbBuilder.show();
                break;
            case R.id.toAddressLayout:
                startActivity(new Intent(activity, AddressShowActivity.class));
                break;
        }
    }


}
