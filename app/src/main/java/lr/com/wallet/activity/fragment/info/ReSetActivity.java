package lr.com.wallet.activity.fragment.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityErrorException;
import com.hunter.wallet.service.SecurityUtils;
import com.hunter.wallet.service.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lr.com.wallet.R;
import lr.com.wallet.activity.InitActivity;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.adapter.AreaCodeAdapter;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.pojo.AreaCodePojo;

/**
 * Created by DT0814 on 2018/9/19.
 */

public class ReSetActivity extends Activity implements View.OnClickListener {
    private Button getCodeBut;
    private TextView areaCode;
    private UserInfo userInfo;
    private TextView phoneText;
    private EditText codeText;
    private Button reSetBut;
    private TextView pinText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_info_fragment_reset_wallet_layout);
        findViewById(R.id.resetPreBut).setOnClickListener(this);
        getCodeBut = findViewById(R.id.getCodeBut);
        getCodeBut.setOnClickListener(this);
        areaCode = findViewById(R.id.areaCode);
        findViewById(R.id.selectAreaCode).setOnClickListener(this);
        try {
            userInfo = SecurityUtils.getUserInfo();
        } catch (SecurityErrorException e) {
            e.printStackTrace();
        }

        reSetBut = findViewById(R.id.reSetBut);
        reSetBut.setOnClickListener(this);
        initPhoneText();
        codeText = findViewById(R.id.codeText);
        pinText = findViewById(R.id.pinText);
        pinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pinText.getText().toString().length() > 0) {
                    reSetBut.setEnabled(true);
                    reSetBut.setBackgroundResource(R.drawable.fillet_fill_blue);
                } else {
                    reSetBut.setEnabled(false);
                    reSetBut.setBackgroundResource(R.drawable.fillet_fill_off_blue);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initPhoneText() {
        phoneText = findViewById(R.id.phoneText);
        String oldPhoneStr = userInfo.getBindMobile().split("-")[1];
        String rex = "****";
        StringBuilder sb = new StringBuilder(oldPhoneStr);
        sb.replace(3, 7, rex);
        phoneText.setText(sb.toString());
    }

    private int oldTime = 30;
    Handler updateCodeBut = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getCodeBut.setText((String) msg.obj);
            if (msg.arg1 == 0) {
                getCodeBut.setEnabled(false);
                getCodeBut.setBackgroundResource(R.drawable.fillet_fill_off_jinse);
            } else {
                getCodeBut.setEnabled(true);
                getCodeBut.setBackgroundResource(R.drawable.fillet_fill_on_jinse);
            }
        }
    };
    Handler showToast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(ReSetActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetPreBut:
                ReSetActivity.this.finish();
                break;
            case R.id.getCodeBut:
                getCodeBut.setEnabled(false);
                getCodeBut.setText("重新获取(" + oldTime + ")");
                Timer oldTimer = new Timer();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        oldTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                if (--oldTime > 0) {
                                    msg.obj = "重新获取(" + oldTime + ")";
                                    msg.arg1 = 0;
                                    updateCodeBut.sendMessage(msg);
                                } else {
                                    oldTime = 30;
                                    this.cancel();
                                    msg.obj = "重新获取";
                                    msg.arg1 = 1;
                                    updateCodeBut.sendMessage(msg);
                                }
                            }
                        }, 1000, 1000);
                    }
                }).start();
                SecurityUtils.sendAuthcode(userInfo.getBindMobile(), new SecurityUtils.UserOperateCallback() {
                    @Override
                    public void onSuccess() {
                        Message message = new Message();
                        message.obj = "验证码发送成功,请注意查收";
                        showToast.sendMessage(message);
                    }

                    @Override
                    public void onFail(String msg) {
                        Message showMsg = new Message();
                        showMsg.obj = "验证码发送失败";
                        showToast.sendMessage(showMsg);
                        oldTime = 30;
                        Message message = new Message();
                        message.obj = "重新获取";
                        message.arg1 = 1;
                        oldTimer.cancel();
                        updateCodeBut.sendMessage(message);
                    }
                });
                break;
            case R.id.selectAreaCode:
                showDialog();
                break;
            case R.id.reSetBut:
                String codeTextStr = codeText.getText().toString();
                if (null == codeTextStr || codeTextStr.length() != 6) {
                    Toast.makeText(ReSetActivity.this, "验证码长度为6位数字", Toast.LENGTH_SHORT).show();
                    break;
                }
                String pinTextStr = pinText.getText().toString();
                if (null == pinTextStr || pinTextStr.length() != 6) {
                    Toast.makeText(ReSetActivity.this, "PIN码长度为6位数字", Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    SecurityUtils.resetWallet(pinTextStr, codeTextStr, new SecurityUtils.UserOperateCallback() {
                        @Override
                        public void onSuccess() {
                            CacheWalletDao.clean();
                            Message message = new Message();
                            message.obj = "重置成功";
                            showToast.sendMessage(message);
                            startActivity(new Intent(ReSetActivity.this, MainFragmentActivity.class));
                            ReSetActivity.this.finish();
                        }

                        @Override
                        public void onFail(String msg) {
                            Message message = new Message();
                            message.obj = "验证码错误或PIN码错误";
                            showToast.sendMessage(message);
                        }
                    });
                } catch (SecurityErrorException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReSetActivity.this);
        View areaView = getLayoutInflater().inflate(R.layout.select_area_code_dialog, null);
        ListView listView = areaView.findViewById(R.id.AreaCodeList);
        List<AreaCodePojo> data = new ArrayList();
        data.add(new AreaCodePojo("中国", "+86"));
        data.add(new AreaCodePojo("中国台湾", "+886"));
        data.add(new AreaCodePojo("中国香港", "+852"));
        data.add(new AreaCodePojo("美国", "+1"));
        AreaCodeAdapter adapter = new AreaCodeAdapter(ReSetActivity.this, R.layout.select_area_code_item, data);
        listView.setAdapter(adapter);
        builder.setView(areaView);
        AlertDialog show = builder.show();
        show.setCancelable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AreaCodePojo areaCodePojo = data.get(position);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        areaCode.setText(areaCodePojo.getNumber());
                    }
                });
                show.dismiss();
            }
        });
        areaView.findViewById(R.id.exitBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }
}
