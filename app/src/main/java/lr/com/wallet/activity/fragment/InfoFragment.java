package lr.com.wallet.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import lr.com.wallet.R;
import lr.com.wallet.activity.fragment.info.AgreementActivity;
import lr.com.wallet.activity.fragment.info.ContactsActivity;
import lr.com.wallet.activity.fragment.info.GuanYuActivity;
import lr.com.wallet.activity.fragment.info.HelpActivity;
import lr.com.wallet.activity.fragment.info.ReSetActivity;
import lr.com.wallet.activity.fragment.info.RebindPhoneActivity;
import lr.com.wallet.activity.fragment.info.UnlockActivity;
import lr.com.wallet.activity.fragment.info.UpdatePinActivity;

/**
 * Created by dt0814 on 2018/7/14.
 */

public class InfoFragment extends Fragment implements View.OnClickListener {
    private LayoutInflater inflater;
    private FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_info_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        this.inflater = inflater;
        view.findViewById(R.id.guanyu).setOnClickListener(this);
        view.findViewById(R.id.contacts).setOnClickListener(this);
        view.findViewById(R.id.message).setOnClickListener(this);
        view.findViewById(R.id.helpLayout).setOnClickListener(this);
        view.findViewById(R.id.agreement).setOnClickListener(this);
        view.findViewById(R.id.updatePIN).setOnClickListener(this);
        view.findViewById(R.id.reBindPhone).setOnClickListener(this);
        view.findViewById(R.id.reSetWallet).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guanyu:
                startActivity(new Intent(activity, GuanYuActivity.class));
                break;
            case R.id.agreement:
                startActivity(new Intent(activity, AgreementActivity.class));
                break;
            case R.id.helpLayout:
                startActivity(new Intent(activity, HelpActivity.class));
                break;
            case R.id.contacts:
                startActivity(new Intent(activity, ContactsActivity.class));
                break;
            case R.id.updatePIN:
                startActivity(new Intent(activity, UpdatePinActivity.class));
                break;
            case R.id.reBindPhone:
                startActivity(new Intent(activity, RebindPhoneActivity.class));
                break;
            case R.id.reSetWallet:
                startActivity(new Intent(activity, ReSetActivity.class));
                break;
            default:
                Toast.makeText(activity, "          功能开发中        ", Toast.LENGTH_SHORT).show();
        }
    }
}
