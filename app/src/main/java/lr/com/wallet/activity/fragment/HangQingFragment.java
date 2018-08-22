package lr.com.wallet.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunter.wallet.service.EthWallet;
import com.hunter.wallet.service.SecurityService;
import com.hunter.wallet.service.TeeErrorException;

import org.web3j.crypto.CipherException;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.ConvertPojo;

/**
 * Created by DT0814 on 2018/8/15.
 */

public class HangQingFragment extends Fragment {
    private View view;
    private LayoutInflater inflater;
    private FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hangqiang_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        this.inflater = inflater;
        List<EthWallet> walletList = new ArrayList<>();
        try {
            walletList = SecurityService.getWalletList();
        } catch (TeeErrorException e) {
            e.printStackTrace();
        }
        for (EthWallet ethWallet : walletList) {
            Log.i("", ethWallet.toString());
        }

        view.findViewById(R.id.clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    for (EthWallet ethWallet : SecurityService.getWalletList()) {
                        Log.i("", ethWallet.toString());
                        try {
                            SecurityService.deleteWallet(ethWallet.getId(), "woaini");
                        } catch (TeeErrorException e) {
                            SecurityService.deleteWallet(ethWallet.getId(), "123456");
                            e.printStackTrace();
                        }
                    }
                    for (ETHWallet ethWallet : WalletDao.getAllWallet()) {
                        WalletDao.deleteWallet(ethWallet);
                    }

                } catch (TeeErrorException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}
