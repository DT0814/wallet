package lr.com.wallet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.utils.DateUtils;
import lr.com.wallet.utils.NumberUtils;

/**
 * Created by lw on 2017/4/14.
 */

public class TxAdapter extends ArrayAdapter {
    private final int resourceId;
    private ETHWallet wallet;
    private CoinPojo coin;

    public TxAdapter(Context context, int textViewResourceId, List<TxBean> objects
            , ETHWallet wallet, CoinPojo coin) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.wallet = wallet;
        this.coin = coin;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TxBean item = (TxBean) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

        ImageView icon = (ImageView) view.findViewById(R.id.transcationIcon);
        TextView statusText = view.findViewById(R.id.statusText);
        TextView ethMsg = (TextView) view.findViewById(R.id.ethMsg);
        TextView tranNum = (TextView) view.findViewById(R.id.tranNum);
        tranNum.setText(item.getHash());
        TextView tranTime = (TextView) view.findViewById(R.id.tranTime);
        boolean sta = true;
        //为真代表这次为支出
        if (item.getStatus().equals("1")) {
            BigDecimal ethNum = Convert.fromWei(item.getValue(), Convert.Unit.ETHER);
            if (item.getFrom().equalsIgnoreCase(wallet.getAddress())) {
                icon.setImageResource(R.drawable.pay);
                ethMsg.setText("-\b" + ethNum + "\b\bETH");
                ethMsg.setTextColor(Color.RED);
            } else {
                icon.setImageResource(R.drawable.income);
                ethMsg.setText("+\b" + ethNum + "\b\bETH");
                ethMsg.setTextColor(Color.BLUE);
                sta = false;
            }

            if (null != coin && !coin.getCoinSymbolName().equalsIgnoreCase("eth")) {
                if (null != item.getInput() && item.getInput().length() > 74) {
                    String value = item.getInput().substring(74, item.getInput().length());
                    if (sta) {
                        ethMsg.setText("-\b" + NumberUtils.trnNumber(value) + "\b\b" + coin.getCoinSymbolName());
                    } else {
                        ethMsg.setText("+\b" + NumberUtils.trnNumber(value) + "\b\b" + coin.getCoinSymbolName());
                    }
                }
            }

        } else if (item.getStatus().equals("0")) {
            icon.setImageResource(R.drawable.tx_fail);
            ethMsg.setText("-\b" + item.getValue() + "\b\b" + coin.getCoinSymbolName());
            ethMsg.setTextColor(Color.RED);
            statusText.setText("交易失败");
            statusText.setTextColor(Color.RED);
        } else {
            icon.setImageResource(R.drawable.jinxingzhong);
            ethMsg.setText("-\b" + item.getValue() + "\b\b" + coin.getCoinSymbolName());
            ethMsg.setTextColor(Color.RED);
            statusText.setText("交易中");
            statusText.setTextColor(Color.BLUE);
        }
        long l = new Long(item.getTimeStamp()) * 1000;
        tranTime.setText(DateUtils.getDateFormatByString(l));

        return view;
    }

}