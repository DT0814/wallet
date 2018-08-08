package lr.com.wallet.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.WalletAdapter;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;

/**
 * Created by dt0814 on 2018/7/18.
 */

public class WalletFragment extends Fragment implements View.OnClickListener {
    private LayoutInflater inflater;
    private Activity activity;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        activity = getActivity();
        view = inflater.inflate(R.layout.wallet_fragment, null);
        LinearLayout createWallet = view.findViewById(R.id.createWallet);
        createWallet.setOnClickListener(this);
        LinearLayout importBut = view.findViewById(R.id.inWallet);
        importBut.setOnClickListener(this);

        initWalletListView();
        return view;
    }

    private void initWalletListView() {
        ListView listView = (ListView) view.findViewById(R.id.walletList);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter((ListAdapter) msg.obj);
            }
        };

        List<ETHWallet> list = WalletDao.getAllWallet();
        WalletAdapter adapter = new WalletAdapter(activity, R.layout.wallet_list_view, list, activity);
        Message msg = new Message();
        msg.obj = adapter;
        handler.sendMessage(msg);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inWallet:
                Intent intent = new Intent(activity, ImportActivity.class);
                startActivity(intent);
                break;
            case R.id.createWallet:
                startActivity(new Intent(activity, CreateWalletActivity.class));
                break;
        }
    }
}
