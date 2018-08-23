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
        CoinPojo item = (CoinPojo) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        Log.i("CoinPojoItem", item.toString());
        ImageView icon = (ImageView) view.findViewById(R.id.CoinAddIcon);
        TextView coinName = (TextView) view.findViewById(R.id.coinSymbolName);
        TextView coinLongName = (TextView) view.findViewById(R.id.coinAddName);
        Switch swith = (Switch) view.findViewById(R.id.addCoinSwitch);
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
                    CoinPojo coinPojo = CoinDao.deleteConinPojo(item);
                    //删除缓存 待做
                    // TxCacheDao.delete(WalletDao.getCurrentWallet().getId().toString(), item.getCoinId().toString());
                }
            }
        });

        return view;
    }

}
