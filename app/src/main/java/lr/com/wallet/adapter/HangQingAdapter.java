package lr.com.wallet.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.pojo.Price;
import lr.com.wallet.utils.SharedPreferencesUtils;
import lr.com.wallet.utils.Type;

public class HangQingAdapter extends RecyclerView.Adapter<HangQingAdapter.ViewHolder> {

    private List<Price> data;
    private int percentChangeType = 1;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_hangqing_fragment_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        percentChangeType = SharedPreferencesUtils.getInt("hangQing", "percentChangeType");
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Price price = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onBindViewHolder", price.toString());
            }
        });
        holder.coinName.setText(price.getName());
        holder.coinSymName.setText(price.getSymbol());
        holder.priceCNY.setText("¥" + price.getPriceCNY());
        holder.priceUSD.setText("" + price.getPriceUSD());
        double percentChange = 0;
        switch (percentChangeType) {
            case Type.HANGQING_PERCENT_CHANGE_1H:
                percentChange = price.getPercentChange1h();
                break;
            case Type.HANGQING_PERCENT_CHANGE_24H:
                percentChange = price.getPercentChange24h();
                break;
            case Type.HANGQING_PERCENT_CHANGE_7D:
                percentChange = price.getPercentChange7d();
                break;
        }

        if (percentChange > 0) {
            holder.icon.setImageResource(R.drawable.js);
            holder.view.setBackgroundResource(R.drawable.fillet_fill_green);
        } else {
            holder.icon.setImageResource(R.drawable.jx);
            holder.view.setBackgroundResource(R.drawable.fillet_fill_rad);
        }
        holder.num.setText(Math.abs(percentChange) + "%");
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
        return null == data ? 0 : data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView coinName;
        TextView coinSymName;
        TextView priceUSD;
        TextView priceCNY;
        ImageView icon;
        TextView num;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            coinName = itemView.findViewById(R.id.coinName);
            coinSymName = itemView.findViewById(R.id.coinSymName);
            priceUSD = itemView.findViewById(R.id.priceUSD);
            priceCNY = itemView.findViewById(R.id.priceCNY);
            icon = itemView.findViewById(R.id.icon);
            num = itemView.findViewById(R.id.num);
            view = itemView.findViewById(R.id.bgLayout);
        }
    }
}