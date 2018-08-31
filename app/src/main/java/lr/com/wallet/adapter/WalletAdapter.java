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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ETHWallet item = (ETHWallet) getItem(position);
        View bg = holder.bg;
        ImageView icon = holder.icon;
        TextView walletName = holder.walletName;
        TextView tranNum = holder.tranNum;
        TextView walletCacheNum = holder.walletCacheNum;
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

        switch (item.getId().intValue() % 2) {
            case 1:
                icon.setImageResource(R.drawable.touxiang2);
                break;
            case 0:
                icon.setImageResource(R.drawable.touxiang);
                break;
        }
        walletName.setText(item.getName());
        tranNum.setText(item.getAddress());
        if (null != item.getNum()) {
            walletCacheNum.setText(item.getNum().toString());
        }
        return convertView;
    }

    private class ViewHolder {
        View bg;
        ImageView icon;
        TextView walletName;
        TextView tranNum;
        TextView walletCacheNum;

        public ViewHolder(View view) {
            bg = view.findViewById(R.id.bg);
            icon = (ImageView) view.findViewById(R.id.wallet_item_img);
            walletName = (TextView) view.findViewById(R.id.wallet_item_name);
            tranNum = (TextView) view.findViewById(R.id.wallet_item_address);
            walletCacheNum = view.findViewById(R.id.walletCacheNum);
        }
    }
}
