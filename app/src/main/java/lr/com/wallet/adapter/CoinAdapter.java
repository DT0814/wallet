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
import lr.com.wallet.pojo.CoinPojo;

/**
 * Created by lw on 2017/4/14.
 */

public class CoinAdapter extends ArrayAdapter {
    private final int resourceId;

    public CoinAdapter(Context context, int textViewResourceId, List<CoinPojo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
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
        CoinPojo item = (CoinPojo) getItem(position);
        ImageView icon = holder.icon;
        switch (item.getCoinSymbolName()) {
            case "ETH":
                icon.setImageResource(R.drawable.coin_eth);
                break;
            case "REP":
                icon.setImageResource(R.drawable.coin_rep_icon);
                break;
            case "MKR":
                icon.setImageResource(R.drawable.coin_mkr_icon);
                break;
            case "GNT":
                icon.setImageResource(R.drawable.coin_gnt_icon);
                break;
            case "1ST":
                icon.setImageResource(R.drawable.coin_onest_icon);
                break;
            case "KBI":
                icon.setImageResource(R.drawable.coin_kib_icon);
                break;
            default:
                icon.setImageResource(R.drawable.coin_eth);
                break;
        }

        TextView coinName = holder.coinName;
        coinName.setText(item.getCoinSymbolName());
        TextView coinValue = holder.coinValue;
        if (null != item.getCoinBalance()) {
            coinValue.setText("≈\b¥\b" + item.getCoinBalance());
        }
        TextView coinNum = holder.coinNum;
        String coinCount = item.getCoinCount();
        if (coinCount.indexOf(".") != -1 && coinCount.indexOf(".") + 7 < coinCount.length()) {
            coinCount = coinCount.substring(0, coinCount.indexOf(".") + 7);
        }
        coinNum.setText(coinCount);
        return convertView;
    }

    private class ViewHolder {
        TextView coinName;
        TextView coinValue;
        TextView coinNum;
        ImageView icon;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.CoinIcon);
            coinName = (TextView) view.findViewById(R.id.coinName);
            coinValue = view.findViewById(R.id.coinValue);
            coinNum = view.findViewById(R.id.coinNum);
        }
    }
}
