package lr.com.wallet.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import lr.com.wallet.R;

public class ChooseMnemonicAdapter extends RecyclerView.Adapter<ChooseMnemonicAdapter.ViewHolder> {

    private List<String> myList;
    private Button mnemonic_confirm;
    private MnemonicAdapter mnemonicAdapter;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        TextView mnemonicName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            mnemonicName = itemView.findViewById(R.id.choose_mnemonic_name);
        }
    }

    public ChooseMnemonicAdapter(List<String> list, Button mnemonic_confirm) {
        myList = list;
        this.mnemonic_confirm = mnemonic_confirm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mnemonic_choose_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                String mnemonic = myList.get(position);
                removeData(position);
                mnemonicAdapter.addData(mnemonicAdapter.getItemCount(), mnemonic);
                if (myList.size() != 12) {
                    mnemonic_confirm.setEnabled(false);
                    mnemonic_confirm.setBackgroundResource(R.drawable.fillet_fill_off_blue);
                } else {
                    mnemonic_confirm.setEnabled(true);
                    mnemonic_confirm.setBackgroundResource(R.drawable.fillet_fill_color_promary);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String mnemonic = myList.get(position);
        holder.mnemonicName.setText(mnemonic);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public void addData(int position, String data) {
        myList.add(position, data);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        myList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public MnemonicAdapter getMnemonicAdapter() {
        return mnemonicAdapter;
    }

    public void setMnemonicAdapter(MnemonicAdapter mnemonicAdapter) {
        this.mnemonicAdapter = mnemonicAdapter;
    }

    public List<String> getMyList() {
        return myList;
    }

    public void setMyList(List<String> myList) {
        this.myList = myList;
    }
}