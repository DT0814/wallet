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
import lr.com.wallet.pojo.Contacts;

/**
 * Created by lw on 2017/4/14.
 */

public class ContactsAdapter extends ArrayAdapter {
    private final int resourceId;

    public ContactsAdapter(Context context, int textViewResourceId, List<Contacts> objects) {
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
        Contacts item = (Contacts) getItem(position);
        holder.address.setText(item.getAddress());
        holder.name.setText(item.getName());
        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView address;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            address = (TextView) view.findViewById(R.id.address);
        }
    }
}
