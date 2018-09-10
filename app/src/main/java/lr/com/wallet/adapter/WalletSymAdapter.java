package lr.com.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.pojo.ETHCacheWallet;

/**
 * Created by lw on 2017/4/14.
 */

public class WalletSymAdapter extends RecyclerView.Adapter<WalletSymAdapter.ViewHolder> {
    private List<ETHCacheWallet> walletList;
    private ETHCacheWallet currEth;

    public WalletSymAdapter(List<ETHCacheWallet> walletList) {
        this.currEth = CacheWalletDao.getCurrentWallet();
        this.walletList = walletList;
    }

    private static DrawerLayout drawerLayout;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View walletView;
        ImageView touxiang;
        TextView name;

        public ViewHolder(View view) {
            super(view);
            walletView = view;
            touxiang = view.findViewById(R.id.touxiang);
            name = view.findViewById(R.id.name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_list_sym_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.walletView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CacheWalletDao.writeCurrentJsonWallet(walletList.get(position));
                Intent intent = new Intent(context, MainFragmentActivity.class);
                context.startActivity(intent);
                Log.i("WalletSymAdapter:OnClickposition", position + "");
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ETHCacheWallet wallet = walletList.get(position);
        holder.name.setText(wallet.getName());

        if (currEth.getId().intValue() == wallet.getId().intValue()) {
            holder.walletView.setBackgroundResource(R.color.background_gray);
        }

        switch (wallet.getId().intValue() % 2) {
            case 1:
                holder.touxiang.setImageResource(R.drawable.touxiang2);
                break;
            case 0:
                holder.touxiang.setImageResource(R.drawable.touxiang);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public static void setDrawerLayout(DrawerLayout layout) {
        drawerLayout = layout;
    }

}
