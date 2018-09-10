package lr.com.wallet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.pojo.CoinPojo;

/**
 * Created by lw on 2017/4/14.
 */

public class CoinAddAdapter extends ArrayAdapter {
    private final int resourceId;
    private List<CoinPojo> coinPojos;

    public CoinAddAdapter(Context context, int textViewResourceId, List<CoinPojo> objects, List<CoinPojo> coinPojos) {
        super(context, textViewResourceId, objects);
        this.coinPojos = coinPojos;
        for (CoinPojo coinPojo : coinPojos) {
            Log.i("coinPojos", coinPojo.toString());
        }
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
        Log.i("CoinPojoItem", item.toString());
        ImageView icon = holder.icon;
        TextView coinName = holder.coinName;
        TextView coinLongName = holder.coinLongName;
        Switch swith = (Switch) holder.swith;
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
        coinName.setText(item.getCoinSymbolName());
        coinLongName.setText(item.getCoinName());
        boolean contains = coinPojos.contains(item);
        Log.i("contains", contains + "");
        if (contains) {
            swith.setChecked(true);
        } else {
            swith.setChecked(false);
        }
        swith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //add
                    CoinDao.addCoinPojo(item);
                } else {
                    //delete
                    CoinPojo coinPojo = CoinDao.deleteCoinPojo(item);
                    //删除缓存 待做
                    // TxCacheDao.delete(CacheWalletDao.getCurrentWallet().getId().toString(), item.getCoinId().toString());
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView icon;
        TextView coinName;
        TextView coinLongName;
        Switch swith;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.CoinAddIcon);
            coinName = (TextView) view.findViewById(R.id.coinSymbolName);
            coinLongName = (TextView) view.findViewById(R.id.coinAddName);
            swith = (Switch) view.findViewById(R.id.addCoinSwitch);
        }


    }
}
