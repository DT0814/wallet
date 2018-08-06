package lr.com.wallet.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lr.com.wallet.R;
import lr.com.wallet.pojo.CoinPojo;

/**
 * Created by DT0814 on 2018/8/6.
 */

public class CoinInfoNoTxlistFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("", "没有数据");
        View view = inflater.inflate(R.layout.coin_info_no_txlist_fragment, null);

        return view;
    }
}
