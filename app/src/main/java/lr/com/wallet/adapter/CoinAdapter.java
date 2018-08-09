package lr.com.wallet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.common.BitMatrix;

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
        CoinPojo item = (CoinPojo) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView icon = (ImageView) view.findViewById(R.id.CoinIcon);
        switch (item.getCoinSymbolName()) {
            case "ETH":
                icon.setImageResource(R.drawable.eth_coin);
                break;
            case "REP":
                icon.setImageResource(R.drawable.rep_icon);
                break;
            case "MKR":
                icon.setImageResource(R.drawable.mkr_icon);
                break;
            case "GNT":
                icon.setImageResource(R.drawable.gnt_icon);
                break;
            case "1ST":
                icon.setImageResource(R.drawable.onest_icon);
                break;
            default:
                icon.setImageResource(R.drawable.eth_coin);
                break;
        }

        TextView coinName = (TextView) view.findViewById(R.id.coinName);
        coinName.setText(item.getCoinSymbolName());
        TextView coinValue = view.findViewById(R.id.coinValue);
        if (null != item.getCoinBalance()) {
            coinValue.setText("≈\b¥\b" + item.getCoinBalance());
        }
        TextView coinNum = view.findViewById(R.id.coinNum);
        String coinCount = item.getCoinCount();
        if (coinCount.indexOf(".") != -1 && coinCount.indexOf(".") + 7 < coinCount.length()) {
            coinCount = coinCount.substring(0, coinCount.indexOf(".") + 7);
        }
        /*if (null != coinCount && coinCount.length() > 10) {
            coinNum.setTextSize(14);
        }*/
        coinNum.setText(coinCount);
        return view;
    }

}
