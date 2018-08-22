package lr.com.wallet.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hunter.wallet.service.EthWallet;
import com.hunter.wallet.service.SecurityService;
import com.hunter.wallet.service.TeeErrorException;

import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.CreateWalletActivity;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;

/**
 * Created by DT0814 on 2018/8/22.
 */

public class NoHaveWalletFragment extends Fragment {
    private View view;
    private LayoutInflater inflater;
    private FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.no_have_wallet_layout, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        this.inflater = inflater;
        Button button = view.findViewById(R.id.createBut);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, CreateWalletActivity.class));
            }
        });
        return view;
    }
}
