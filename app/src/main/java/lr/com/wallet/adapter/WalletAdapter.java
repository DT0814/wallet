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
        View bg = view.findViewById(R.id.bg);
        switch (position % 5) {
            case 4:
                bg.setBackgroundResource(R.drawable.walletbg_5);
                break;
            case 3:
                bg.setBackgroundResource(R.drawable.walletbg_4);
                break;
            case 2:
                bg.setBackgroundResource(R.drawable.walletbg_1);
                break;
            case 1:
                bg.setBackgroundResource(R.drawable.walletbg_3);
                break;
            case 0:
                bg.setBackgroundResource(R.drawable.walletbg_2);
                break;
        }
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
        TextView walletCacheNum = view.findViewById(R.id.walletCacheNum);
        if (null != item.getNum()) {
            walletCacheNum.setText(item.getNum().toString());
        }
        return view;
    }

}
