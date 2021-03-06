package lr.com.wallet.activity.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hunter.wallet.service.SecurityUtils;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.CreateWalletActivity;
import lr.com.wallet.activity.ImportActivity;
import lr.com.wallet.activity.WalletInfoActivity;
import lr.com.wallet.adapter.WalletAdapter;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.dao.ETHWalletDao;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.JsonUtils;

/**
 * Created by dt0814 on 2018/7/18.
 */

public class WalletFragment extends Fragment implements View.OnClickListener {
    private Activity activity;
    private View view;
    private WalletAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        initWalletListView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        view = inflater.inflate(R.layout.main_wallet_fragment, null);
        RelativeLayout createWallet = view.findViewById(R.id.createWallet);
        createWallet.setOnClickListener(this);
        RelativeLayout importBut = view.findViewById(R.id.inWallet);
        importBut.setOnClickListener(this);
        initWalletListView();
        return view;
    }

    private void initWalletListView() {
        ListView listView = view.findViewById(R.id.walletList);

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter((ListAdapter) msg.obj);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ETHCacheWallet ethCacheWallet = (ETHCacheWallet) parent.getAdapter().getItem(position);
                        Intent intent = new Intent(activity, WalletInfoActivity.class);
                        intent.putExtra("wallet", JsonUtils.objectToJson(ethCacheWallet));
                        startActivity(intent);
                    }
                });

            }
        };

        List<ETHCacheWallet> list = CacheWalletDao.getAllWallet();
        WalletAdapter adapter = new WalletAdapter(activity, R.layout.wallet_item, list, activity);
        Message msg = new Message();
        msg.obj = adapter;
        handler.sendMessage(msg);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inWallet:
                SecurityUtils.checkEnv(activity, new SecurityUtils.CheckEnvCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(activity, ImportActivity.class));
                        /*activity.finish();*/
                    }

                    @Override
                    public void onFail() {

                    }
                });
                break;
            case R.id.createWallet:
                SecurityUtils.checkEnv(activity, new SecurityUtils.CheckEnvCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(activity, CreateWalletActivity.class));
                       /* activity.finish();*/
                    }

                    @Override
                    public void onFail() {

                    }
                });
                break;
        }
    }

}
