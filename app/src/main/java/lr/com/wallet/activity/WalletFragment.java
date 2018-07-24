package lr.com.wallet.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.TransactionAdapter;
import lr.com.wallet.adapter.WalletAdapter;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TransactionBean;
import lr.com.wallet.pojo.TransactionPojo;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.Md5Utils;
import lr.com.wallet.utils.TransactionUtils;

/**
 * Created by dt0814 on 2018/7/18.
 */

public class WalletFragment extends Fragment implements View.OnClickListener {
    private LayoutInflater inflater;
    private Activity activity;
    private View view;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        activity = getActivity();
        view = inflater.inflate(R.layout.wallet_fragment, null);
        Button createWallet = view.findViewById(R.id.createWallet);
        createWallet.setOnClickListener(this);
        Button importBut = view.findViewById(R.id.inWallet);
        importBut.setOnClickListener(this);

        initWalletListView();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initWalletListView() {
        ListView listView = (ListView) view.findViewById(R.id.walletList);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter((ListAdapter) msg.obj);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ETHWallet itemAtPosition = (ETHWallet) adapterView.getItemAtPosition(i);
                     /*   Intent intent = new Intent(activity, TxInfoActivity.class);
                        intent.putExtra("obj", JsonUtils.objectToJson(itemAtPosition));
                        startActivity(intent);*/
                        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(activity);
                        normalDialog.setTitle("提示");
                        normalDialog.setMessage("您要切换钱包么");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        WalletDao.writeCurrentJsonWallet(itemAtPosition);
                                        Intent intent = new Intent(activity, MainFragmentActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        normalDialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        normalDialog.show();
                    }
                });
            }
        };

        List<ETHWallet> list = WalletDao.getAllWallet();
        WalletAdapter adapter = new WalletAdapter(activity, R.layout.wallet_list_view, list);
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
