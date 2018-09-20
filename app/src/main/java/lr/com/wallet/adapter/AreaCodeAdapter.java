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
import lr.com.wallet.pojo.AreaCodePojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.ETHWalletUtils;

/**
 * Created by lw on 2017/4/14.
 */

public class AreaCodeAdapter extends ArrayAdapter {
    private final int resourceId;
    private List<AreaCodePojo> data;

    public AreaCodeAdapter(Context context, int textViewResourceId, List<AreaCodePojo> data) {
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
        AreaCodePojo areaCodePojo = data.get(position);
        holder.name.setText(areaCodePojo.getName());
        holder.number.setText(areaCodePojo.getNumber());
        if (position == 3) {
            holder.bottom.setVisibility(View.GONE);
        } else {
            holder.bottom.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView number;
        View bottom;

        public ViewHolder(View view) {
            name = view.findViewById(R.id.name);
            number = view.findViewById(R.id.number);
            bottom = view.findViewById(R.id.bottom);
        }
    }
}
