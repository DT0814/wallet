package lr.com.wallet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

public class WalletSymAdapter extends ArrayAdapter {
    private final int resourceId;

    public WalletSymAdapter(Context context, int textViewResourceId, List<ETHWallet> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ETHWallet item = (ETHWallet) getItem(position);
        Log.i("ETHWallet+++++++++++++++", item.toString());
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView icon = (ImageView) view.findViewById(R.id.touxiang);
        switch (item.getId().intValue() % 2) {
            case 1:
                icon.setImageResource(R.drawable.touxiang2);
                break;
            case 0:
                icon.setImageResource(R.drawable.touxiang);
                break;
        }
        TextView walletName = (TextView) view.findViewById(R.id.name);
        walletName.setText(item.getName());
        return view;
    }

}
