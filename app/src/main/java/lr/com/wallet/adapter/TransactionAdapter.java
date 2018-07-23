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
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TransactionBean;
import lr.com.wallet.utils.DateUtils;

/**
 * Created by lw on 2017/4/14.
 */

public class TransactionAdapter extends ArrayAdapter {
    private final int resourceId;
    ETHWallet wallet;

    public TransactionAdapter(Context context, int textViewResourceId, List<TransactionBean> objects, ETHWallet wallet) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.wallet = wallet;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TransactionBean item = (TransactionBean) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

        ImageView icon = (ImageView) view.findViewById(R.id.transcationIcon);

        TextView ethMsg = (TextView) view.findViewById(R.id.ethMsg);
        BigDecimal ethNum = Convert.fromWei(item.getValue(), Convert.Unit.ETHER);
        //为真代表这次为支出
        if (item.getFrom().equalsIgnoreCase(wallet.getAddress())) {
            icon.setImageResource(R.drawable.pay);
            ethMsg.setText("-\b" + ethNum + "\b\bETH");
            ethMsg.setTextColor(Color.RED);
        } else {
            icon.setImageResource(R.drawable.income);
            ethMsg.setText("+\b" + ethNum + "\b\bETH");
            ethMsg.setTextColor(Color.BLUE);
        }

        TextView tranNum = (TextView) view.findViewById(R.id.tranNum);
        tranNum.setText(item.getHash());

        TextView tranTime = (TextView) view.findViewById(R.id.tranTime);
        long l = new Long(item.getTimeStamp()) * 1000;
        tranTime.setText(DateUtils.getDateFormatByString(l));
        return view;
    }

}
