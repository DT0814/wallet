package lr.com.wallet.activity.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityService;
import com.hunter.wallet.service.TeeErrorException;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.CreateWalletActivity;
import lr.com.wallet.activity.ImportActivity;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.activity.WalletInfoActivity;
import lr.com.wallet.adapter.WalletAdapter;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.JsonUtils;

/**
 * Created by dt0814 on 2018/7/18.
 */

public class WalletFragment extends Fragment implements View.OnClickListener {
    private LayoutInflater inflater;
    private Activity activity;
    private View view;
    private PopupMenu popupMenu;
    ClipboardManager clipManager;
    private AlertDialog.Builder alertbBuilder;

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
        ListView listView = view.findViewById(R.id.walletList);

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter((ListAdapter) msg.obj);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ETHWallet ethWallet = (ETHWallet) parent.getAdapter().getItem(position);
                        Intent intent = new Intent(activity, WalletInfoActivity.class);
                        intent.putExtra("wallet", JsonUtils.objectToJson(ethWallet));
                        startActivity(intent);
                    }
                });

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
