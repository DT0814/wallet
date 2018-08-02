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
        CoinPojo item = (CoinPojo) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView icon = (ImageView) view.findViewById(R.id.CoinIcon);
        icon.setImageResource(R.drawable.star_1);
        TextView coinName = (TextView) view.findViewById(R.id.coinName);
        coinName.setText(item.getCoinSymbolName());
        TextView coinAddress = (TextView) view.findViewById(R.id.coinAddress);
        coinAddress.setText(item.getCoinAddress());
        TextView coinNum = view.findViewById(R.id.coinNum);
        coinNum.setText(item.getCoinCount());
        return view;
    }

}
