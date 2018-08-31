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
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import lr.com.wallet.R;
import lr.com.wallet.adapter.HangQingAdapter;
import lr.com.wallet.pojo.Price;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.HTTPUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;
import lr.com.wallet.utils.Type;

/**
 * Created by DT0814 on 2018/8/15.
 */

public class HangQingFragment extends Fragment {
    private View view;
    private Activity activity;
    private RecyclerView recyclerView;
    private HangQingAdapter adapter;
    List<Price> data;
    private int page = 1;
    private boolean last = false;
    private Timer timer;
    private boolean isFlash = false;
    int type;

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause______HomeFragment");
        timer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("onResume:::", isFlash + "");
                if (!isFlash) {
                    int num = (null == data ? 10 : data.size());
                    List<Price> list = HTTPUtils.getList("http://120.79.165.113:9099/getPrice?start=0&limit=" + num, Price.class);
                    list.forEach(n -> {
                        Log.i("onResume:::", n.toString());
                    });
                    data = list;
                    Log.i("onResume:::", data.size() + "");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.update(data);
                        }
                    });
                    SharedPreferencesUtils.writeString("hangQing", "cache", JsonUtils.objectToJson(data.subList(0, 10)));
                }
            }
        }, 3000, 15000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            recyclerView.setAdapter(adapter);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_hangqing_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        recyclerView = view.findViewById(R.id.hangQingList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        type = SharedPreferencesUtils.getInt("hangQing", "percentChangeType");
        initFloatText();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.canScrollVertically(1)) {
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<Price> list = getPage();
                            data.addAll(list);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.update(data);
                                }
                            });
                        }
                    }).start();

                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = SharedPreferencesUtils.getString("hangQing", "cache");
                if (null == jsonStr || jsonStr.trim().equals("")) {
                    data = getPage();
                    SharedPreferencesUtils.writeString("hangQing", "cache", JsonUtils.objectToJson(data));
                } else {
                    Log.i("HangQing", "缓存命中");
                    data = JsonUtils.jsonToList(jsonStr, Price.class);
                }
                adapter = new HangQingAdapter(data);
                handler.sendMessage(new Message());
            }
        }).start();
        return view;
    }

    private void initFloatText() {

        TextView floatText = view.findViewById(R.id.floatText);

        switch (type) {
            case 1:
                floatText.setText("涨跌幅(\b1h\b)");
                break;
            case 2:
                floatText.setText("涨跌幅(\b24h\b)");
                break;
            case 3:
                floatText.setText("涨跌幅(\b7d\b)");
                break;
        }

        floatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type++;
                type %= 3;
                switch (type) {
                    case 1:
                        floatText.setText("涨跌幅(\b1h\b)");
                        SharedPreferencesUtils.writeInt("hangQing", "percentChangeType", Type.HANGQING_PERCENT_CHANGE_1H);
                        break;
                    case 2:
                        floatText.setText("涨跌幅(\b24h\b)");
                        SharedPreferencesUtils.writeInt("hangQing", "percentChangeType", Type.HANGQING_PERCENT_CHANGE_24H);
                        break;
                    case 0:
                        floatText.setText("涨跌幅(\b7d\b)");
                        SharedPreferencesUtils.writeInt("hangQing", "percentChangeType", Type.HANGQING_PERCENT_CHANGE_7D);
                        break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        data = getPage();
                        adapter = new HangQingAdapter(data);
                        handler.sendMessage(new Message());
                    }
                }).start();
            }
        });

    }

    public List<Price> getPage() {
        isFlash = true;
        List<Price> list = HTTPUtils.getList("http://120.79.165.113:9099/getPrice?start=" + (page - 1) * 10 + "&limit=10", Price.class);
        if (null != list) {
            if (list.size() == 10) {
                page++;
            } else {
                last = true;
            }
            list.forEach(System.out::println);
            isFlash = false;
            return list;
        } else {
            isFlash = false;
            return null;
        }
    }

}
