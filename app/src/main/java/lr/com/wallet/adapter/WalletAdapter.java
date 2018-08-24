package lr.com.wallet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;

/**
 * Created by lw on 2017/4/14.
 */

public class WalletAdapter extends ArrayAdapter {
    private final int resourceId;
    private Activity activity;

    public WalletAdapter(Context context, int textViewResourceId, List<ETHWallet> objects, Activity activity) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ETHWallet item = (ETHWallet) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView icon = (ImageView) view.findViewById(R.id.wallet_item_img);
        switch (item.getId().intValue() % 2) {
            case 1:
                icon.setImageResource(R.drawable.touxiang2);
                break;
            case 0:
                icon.setImageResource(R.drawable.touxiang);
                break;
        }

        TextView walletName = (TextView) view.findViewById(R.id.wallet_item_name);
        walletName.setText(item.getName());
        TextView tranNum = (TextView) view.findViewById(R.id.wallet_item_address);
        tranNum.setText(item.getAddress());
        ImageButton switchWalletBut = view.findViewById(R.id.switchWalletBut);
        TextView walletCacheNum = view.findViewById(R.id.walletCacheNum);
        if (null != item.getNum()) {
            walletCacheNum.setText(item.getNum().toString());
        }
        switchWalletBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(activity);
                normalDialog.setTitle("提示");
                normalDialog.setMessage("您要切换钱包么");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                WalletDao.writeCurrentJsonWallet(item);
                                Intent intent = new Intent(activity, MainFragmentActivity.class);
                                activity.startActivity(intent);
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
        return view;
    }

}
