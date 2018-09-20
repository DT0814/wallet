package lr.com.wallet.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.ETHWalletUtils;

/**
 * Created by lw on 2017/4/14.
 */

public class WalletAdapter extends ArrayAdapter {
    private final int resourceId;
    private List<ETHCacheWallet> data;

    public WalletAdapter(Context context, int textViewResourceId, List<ETHCacheWallet> data, Activity activity) {
        super(context, textViewResourceId, data);
        resourceId = textViewResourceId;
        this.data = data;
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
        ETHCacheWallet item = (ETHCacheWallet) getItem(position);
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
        ETHWalletUtils.switchTouXiangImg(icon, item.getTongxingID());
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

    public void update(List<ETHCacheWallet> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
