package lr.com.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.Price;
import lr.com.wallet.pojo.TouXiangListItem;
import lr.com.wallet.utils.ETHWalletUtils;

/**
 * Created by lw on 2017/4/14.
 */

public class TouXiangAdapter extends RecyclerView.Adapter<TouXiangAdapter.ViewHolder> {
    private List<TouXiangListItem> data;
    private ETHCacheWallet ethCacheWallet;

    public TouXiangAdapter(List<TouXiangListItem> data, ETHCacheWallet ethCacheWallet) {
        this.data = data;
        this.ethCacheWallet = ethCacheWallet;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton image;
        ImageView changed;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            changed = view.findViewById(R.id.changed);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.change_touxiang_layout_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                data.forEach((n) -> {
                    n.setChanged(false);
                });
                ethCacheWallet.setTongxingID(position);
                data.get(position).setChanged(true);
                update(data);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TouXiangListItem touXiang = data.get(position);
        ETHWalletUtils.switchTouXiangImg(holder.image, touXiang.getId());
        if (touXiang.isChanged()) {
            holder.changed.setVisibility(View.VISIBLE);
        } else {
            holder.changed.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(List<TouXiangListItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
