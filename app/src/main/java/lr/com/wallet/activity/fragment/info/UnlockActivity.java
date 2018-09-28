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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.adapter.AreaCodeAdapter;
import lr.com.wallet.pojo.AreaCodePojo;

/**
 * Created by DT0814 on 2018/9/19.
 */

public class UnlockActivity extends Activity implements View.OnClickListener {
    private Button getCodeBut;
    private TextView areaCode;
    private UserInfo userInfo;
    private TextView phoneText;
    private EditText codeText;
    private Button unlockBut;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_info_fragment_unlock_layout);
        getCodeBut = findViewById(R.id.getCodeBut);
        getCodeBut.setOnClickListener(this);
        areaCode = findViewById(R.id.areaCode);
        try {
            userInfo = SecurityUtils.getUserInfo();
        } catch (SecurityErrorException e) {
            e.printStackTrace();
        }
        unlockBut = findViewById(R.id.unlockBut);
        unlockBut.setOnClickListener(this);
        initPhoneText();
        codeText = findViewById(R.id.codeText);
        codeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (codeText.getText().toString().length() > 0) {
                    unlockBut.setEnabled(true);
                    unlockBut.setBackgroundResource(R.drawable.fillet_fill_blue_on);
                } else {
                    unlockBut.setEnabled(false);
                    unlockBut.setBackgroundResource(R.drawable.fillet_fill_blue_off);
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

    private int time = 30;
    Handler updateCodeBut = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getCodeBut.setText((String) msg.obj);
            if (msg.arg1 == 0) {
                getCodeBut.setEnabled(false);
                getCodeBut.setBackgroundResource(R.drawable.fillet_fill_jinse_off);
            } else {
                getCodeBut.setEnabled(true);
                getCodeBut.setBackgroundResource(R.drawable.fillet_fill_jinse_on);
            }
        }
    };
    Handler showToast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(UnlockActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };
    Timer timer;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getCodeBut:
                getCodeBut.setEnabled(false);
                getCodeBut.setText("重新获取(" + time + ")");
                timer = new Timer();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                if (--time > 0) {
                                    msg.obj = "重新获取(" + time + ")";
                                    msg.arg1 = 0;
                                    updateCodeBut.sendMessage(msg);
                                } else {
                                    time = 30;
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
                        time = 30;
                        Message message = new Message();
                        message.obj = "重新获取";
                        message.arg1 = 1;
                        timer.cancel();
                        updateCodeBut.sendMessage(message);
                    }
                });
                break;
            case R.id.unlockBut:
                String codeTextStr = codeText.getText().toString();
                if (null == codeTextStr || codeTextStr.length() != 6) {
                    Toast.makeText(UnlockActivity.this, "验证码长度为6位", Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    SecurityUtils.unlockPin(codeTextStr, new SecurityUtils.UserOperateCallback() {
                        @Override
                        public void onSuccess() {
                            Message message = new Message();
                            message.obj = "解锁成功";
                            showToast.sendMessage(message);
                            startActivity(new Intent(UnlockActivity.this, MainFragmentActivity.class));
                            UnlockActivity.this.finish();
                        }

                        @Override
                        public void onFail(String msg) {
                            Message message = new Message();
                            message.obj = "验证码错误";
                            timer.cancel();
                            showToast.sendMessage(message);
                            time = 30;
                            Message msg1 = new Message();
                            msg1.obj = "重新获取";
                            msg1.arg1 = 1;
                            updateCodeBut.sendMessage(msg1);
                        }
                    });
                } catch (SecurityErrorException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
