package lr.com.wallet.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.HangQingAdapter;
import lr.com.wallet.pojo.Price;
import lr.com.wallet.utils.HTTPUtils;

/**
 * Created by DT0814 on 2018/8/15.
 */

public class HangQingFragment extends Fragment {
    private View view;
    private Activity activity;
    private RecyclerView recyclerView;
    private HangQingAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_hangqing_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        recyclerView = view.findViewById(R.id.hangQingList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                HangQingAdapter adapter = (HangQingAdapter) msg.obj;
                recyclerView.setAdapter(adapter);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Price> list = HTTPUtils.getList("http://120.79.165.113:9099/getPrice?start=0&limit=10", Price.class);
                adapter = new HangQingAdapter(list);
                Message message = new Message();
                message.obj = adapter;
                handler.sendMessage(message);
                adapter.update(list);
            }
        }).start();
        return view;
    }


}
