package lr.com.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.pojo.ETHWallet;

/**
 * Created by lw on 2017/4/14.
 */

public class WalletAdapter extends ArrayAdapter {
    private final int resourceId;

    public WalletAdapter(Context context, int textViewResourceId, List<ETHWallet> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ETHWallet item = (ETHWallet) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView icon = (ImageView) view.findViewById(R.id.wallet_item_img);
        icon.setImageResource(R.drawable.star_1);
        TextView walletName = (TextView) view.findViewById(R.id.wallet_item_name);
        walletName.setText(item.getName());
        TextView tranNum = (TextView) view.findViewById(R.id.wallet_item_address);
        tranNum.setText(item.getAddress());
        return view;
    }

}
