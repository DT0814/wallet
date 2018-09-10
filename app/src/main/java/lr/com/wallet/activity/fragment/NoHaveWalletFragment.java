package lr.com.wallet.activity.fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunter.wallet.service.SecurityUtils;

import lr.com.wallet.R;
import lr.com.wallet.activity.CreateWalletActivity;
import lr.com.wallet.activity.ImportActivity;

/**
 * Created by DT0814 on 2018/8/22.
 */

public class NoHaveWalletFragment extends Fragment {
    private LayoutInflater inflater;
    private FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_have_wallet_layout, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        this.inflater = inflater;
        view.findViewById(R.id.createBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(activity, CreateWalletActivity.class));
                SecurityUtils.checkEnv(activity, new SecurityUtils.CheckEnvCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(activity, CreateWalletActivity.class));
                    }
                    @Override
                    public void onFail() {

                    }
                });
            }
        });
        view.findViewById(R.id.importBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(activity, ImportActivity.class));
                SecurityUtils.checkEnv(activity, new SecurityUtils.CheckEnvCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(activity, ImportActivity.class));
                    }
                    @Override
                    public void onFail() {

                    }
                });
            }
        });
        AssetManager assets = activity.getAssets();
        Typeface tf = Typeface.createFromAsset(assets, "fonts/franklin_gothic_medium.ttf");
        ((TextView) view.findViewById(R.id.tokenSafe)).setTypeface(tf);
        return view;
    }

}
