package lr.com.wallet.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.pojo.Price;

public class HangQingAdapter extends RecyclerView.Adapter<HangQingAdapter.ViewHolder> {

    private List<Price> data;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_hangqing_fragment_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Price price = data.get(position);
        Log.i("HangQingAdapter:", price.toString());
        holder.coinName.setText(price.getName());
        holder.priceCNY.setText("¥" + price.getPriceCNY());
        holder.priceUSD.setText("" + price.getPriceUSD());
        double percentChange1h = price.getPercentChange1h();
        if (percentChange1h > 0) {
            holder.icon.setImageResource(R.drawable.js);
            holder.view.setBackgroundResource(R.drawable.fillet_fill_green);
        } else {
            holder.icon.setImageResource(R.drawable.jx);
            holder.view.setBackgroundResource(R.drawable.fillet_fill_rad);
        }
        holder.num.setText(Math.abs(percentChange1h) + "%");
    }

    public HangQingAdapter(List<Price> data) {
        this.data = data;
    }

    public void update(List<Price> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView coinName;
        TextView priceUSD;
        TextView priceCNY;
        ImageView icon;
        TextView num;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            coinName = itemView.findViewById(R.id.coinName);
            priceUSD = itemView.findViewById(R.id.priceUSD);
            priceCNY = itemView.findViewById(R.id.priceCNY);
            icon = itemView.findViewById(R.id.icon);
            num = itemView.findViewById(R.id.num);
            view = itemView.findViewById(R.id.bgLayout);
        }
    }
}