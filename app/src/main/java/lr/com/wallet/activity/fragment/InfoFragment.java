package lr.com.wallet.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lr.com.wallet.R;

/**
 * Created by dt0814 on 2018/7/14.
 */

public class InfoFragment extends Fragment {
    private View view;
    private LayoutInflater inflater;
    private FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.info_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        this.inflater = inflater;
        return view;
    }
}
