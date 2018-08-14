package lr.com.wallet.activity;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.WalletAdapter;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.ETHWalletUtils;

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
    private ClipData mClipData;

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

    public void popupmenu(View v) {
        popupMenu.show();
    }

    private void initWalletListView() {
        ListView listView = (ListView) view.findViewById(R.id.walletList);

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter((ListAdapter) msg.obj);
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Vibrator vibrator = (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);
                        vibrator.vibrate(100);
                        clipManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        alertbBuilder = new AlertDialog.Builder(activity);
                        popupMenu = new PopupMenu(activity, view);
                        popupMenu.setGravity(Gravity.RIGHT);
                        Menu menu = popupMenu.getMenu();
                        MenuInflater menuInflater = activity.getMenuInflater();
                        menuInflater.inflate(R.menu.wallet_menu, menu);
                        // 通过XML文件添加菜单项
                        popupMenu.show();
                        ETHWallet ethWallet = (ETHWallet) parent.getAdapter().getItem(position);

                        Log.i("长按", ethWallet.toString());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                View pwdView;
                                switch (item.getItemId()) {
                                    case R.id.deleteWalletBut:
                                        pwdView = inflater.inflate(R.layout.input_pwd_layout, null);
                                        alertbBuilder.setView(pwdView);
                                        alertbBuilder.setTitle("请输入密码").setMessage("").setPositiveButton("确定",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        EditText editText = pwdView.findViewById(R.id.inPwdBut);
                                                        String pwd = editText.getText().toString();
                                                        String privateKey = ETHWalletUtils.derivePrivateKey(ethWallet, pwd);
                                                        if (null == privateKey || privateKey.equals("")) {
                                                            Toast.makeText(activity, "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            mClipData = ClipData.newPlainText("Label", privateKey);
                                                            clipManager.setPrimaryClip(mClipData);
                                                            Toast.makeText(activity, "私钥已经复制到剪切板", Toast.LENGTH_SHORT).show();
                                                            dialog.cancel();
                                                        }
                                                    }
                                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }

                                        }).create();
                                        alertbBuilder.show();
                                        break;
                                }
                                return false;
                            }
                        });
                        return false;
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
