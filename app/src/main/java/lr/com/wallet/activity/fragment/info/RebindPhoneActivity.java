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
import android.util.Log;
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
import lr.com.wallet.activity.WalletInfoActivity;
import lr.com.wallet.adapter.AreaCodeAdapter;
import lr.com.wallet.pojo.AreaCodePojo;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by DT0814 on 2018/9/19.
 */

public class RebindPhoneActivity extends Activity implements View.OnClickListener {
    private String areaCodeStr = "+86-";
    private String newAreaCodeStr = "+86-";
    private Button oldCodeBut;
    private TextView oldPhoneText;
    private TextView areaCode;
    private TextView newAreaCode;
    private EditText newPhoneText;
    private EditText oldCodeText;
    private EditText newCodeText;
    private Button newCodeBut;
    private EditText pinText;
    private Button reBindPhoneBut;
    private UserInfo userInfo;
    private String newPhoneStr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_info_fragment_rebind_phone_layout);
        findViewById(R.id.reBindPhonePreBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RebindPhoneActivity.this.finish();
            }
        });
        try {
            userInfo = SecurityUtils.getUserInfo();
        } catch (SecurityErrorException e) {
            e.printStackTrace();
        }
        initOldPhoneText();
        oldCodeBut = findViewById(R.id.oldCodeBut);
        oldCodeBut.setOnClickListener(this);
        oldCodeText = findViewById(R.id.oldCodeText);
        newPhoneText = findViewById(R.id.newPhoneText);
        newCodeText = findViewById(R.id.newCodeText);
        newCodeBut = findViewById(R.id.newCodeBut);
        newCodeBut.setOnClickListener(this);
        reBindPhoneBut = findViewById(R.id.reBindPhoneBut);
        reBindPhoneBut.setOnClickListener(this);
        pinText = findViewById(R.id.pinText);
        pinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pinText.getText().toString().length() > 0) {
                    reBindPhoneBut.setEnabled(true);
                    reBindPhoneBut.setBackgroundResource(R.drawable.fillet_fill_blue);
                } else {
                    reBindPhoneBut.setEnabled(false);
                    reBindPhoneBut.setBackgroundResource(R.drawable.fillet_fill_off_blue);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.selectAreaCode).setOnClickListener(this);
        findViewById(R.id.newSelectAreaCode).setOnClickListener(this);
        areaCode = findViewById(R.id.areaCode);
        newAreaCode = findViewById(R.id.newAreaCode);
    }

    private void initOldPhoneText() {
        oldPhoneText = findViewById(R.id.oldPhoneText);
        String oldPhoneStr = userInfo.getBindMobile().split("-")[1];
        String rex = "****";
        StringBuilder sb = new StringBuilder(oldPhoneStr);
        sb.replace(3, 7, rex);
        oldPhoneText.setText(sb.toString());
    }

    Handler updateOldCodeBut = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            oldCodeBut.setText((String) msg.obj);
            if (msg.arg1 == 0) {
                oldCodeBut.setEnabled(false);
                oldCodeBut.setBackgroundResource(R.drawable.fillet_fill_off_jinse);
            } else {
                oldCodeBut.setEnabled(true);
                oldCodeBut.setBackgroundResource(R.drawable.fillet_fill_on_jinse);
            }
        }
    };
    Handler updateNewCodeBut = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            newCodeBut.setText((String) msg.obj);
            if (msg.arg1 == 0) {
                newCodeBut.setEnabled(false);
                newCodeBut.setBackgroundResource(R.drawable.fillet_fill_off_jinse);
            } else {
                newCodeBut.setEnabled(true);
                newCodeBut.setBackgroundResource(R.drawable.fillet_fill_on_jinse);
            }
        }
    };
    Handler showToast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(RebindPhoneActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };
    int oldTime = 30;
    int newTime = 30;

    private void showdialog(boolean isOldArea) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RebindPhoneActivity.this);
        View areaView = getLayoutInflater().inflate(R.layout.select_area_code_dialog, null);
        ListView listView = areaView.findViewById(R.id.AreaCodeList);
        List<AreaCodePojo> data = new ArrayList();
        data.add(new AreaCodePojo("中国", "+86"));
        data.add(new AreaCodePojo("中国台湾", "+886"));
        data.add(new AreaCodePojo("中国香港", "+852"));
        data.add(new AreaCodePojo("美国", "+1"));
        AreaCodeAdapter adapter = new AreaCodeAdapter(RebindPhoneActivity.this, R.layout.select_area_code_item, data);
        listView.setAdapter(adapter);
        builder.setView(areaView);
        AlertDialog show = builder.show();
        show.setCancelable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AreaCodePojo areaCodePojo = data.get(position);
                if (isOldArea) {
                    areaCodeStr = areaCodePojo.getNumber() + "-";
                } else {
                    newAreaCodeStr = areaCodePojo.getNumber() + "-";
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (isOldArea) {
                            areaCode.setText(areaCodePojo.getNumber());
                        } else {
                            newAreaCode.setText(areaCodePojo.getNumber());
                        }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectAreaCode:
                showdialog(true);
                break;
            case R.id.newSelectAreaCode:
                showdialog(false);
                break;
            case R.id.oldCodeBut:
                oldCodeBut.setEnabled(false);
                oldCodeBut.setText("重新获取(" + oldTime + ")");
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
                                    updateOldCodeBut.sendMessage(msg);
                                } else {
                                    oldTime = 30;
                                    this.cancel();
                                    msg.obj = "重新获取";
                                    msg.arg1 = 1;
                                    updateOldCodeBut.sendMessage(msg);
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
                        updateOldCodeBut.sendMessage(message);
                    }
                });
                break;
            case R.id.newCodeBut:
                newPhoneStr = (newAreaCodeStr + newPhoneText.getText().toString()).trim();
                if (newPhoneStr.length() != 15) {
                    Toast.makeText(RebindPhoneActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                newCodeBut.setEnabled(false);
                newCodeBut.setText("重新获取(" + newTime + ")");
                Timer newTimer = new Timer();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                if (--newTime > 0) {
                                    msg.obj = "重新获取(" + newTime + ")";
                                    msg.arg1 = 0;
                                    updateNewCodeBut.sendMessage(msg);
                                } else {
                                    newTime = 30;
                                    this.cancel();
                                    msg.obj = "重新获取";
                                    msg.arg1 = 1;
                                    updateNewCodeBut.sendMessage(msg);
                                }
                            }
                        }, 1000, 1000);
                    }
                }).start();

                SecurityUtils.sendAuthcode(newPhoneStr, new SecurityUtils.UserOperateCallback() {
                    @Override
                    public void onSuccess() {
                        Message message = new Message();
                        message.obj = "验证码发送成功,请注意查收";
                        showToast.sendMessage(message);
                    }

                    @Override
                    public void onFail(String msg) {
                        Log.i("验证码发送失败", newPhoneStr);
                        Message showMsg = new Message();
                        showMsg.obj = "验证码发送失败";
                        showToast.sendMessage(showMsg);
                        newTime = 30;
                        newTimer.cancel();
                        Message message = new Message();
                        message.obj = "重新获取";
                        message.arg1 = 1;
                        updateNewCodeBut.sendMessage(message);
                    }
                });
                break;
            case R.id.reBindPhoneBut:
                String pin = pinText.getText().toString();
                String oldCodeString = oldCodeText.getText().toString();
                String newCodeString = newCodeText.getText().toString();
                if (null == pin || pin.length() != 6) {
                    Toast.makeText(RebindPhoneActivity.this, "PIN长度错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (null == oldCodeString || oldCodeString.length() != 6) {
                    Toast.makeText(RebindPhoneActivity.this, "验证码长度错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (null == newCodeString || newCodeString.length() != 6) {
                    Toast.makeText(RebindPhoneActivity.this, "验证码长度错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (null == newPhoneStr || newPhoneStr.length() != 15) {
                    Toast.makeText(RebindPhoneActivity.this, "手机号长度不对", Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    Message message = new Message();
                    SecurityUtils.rebindMobile(pin, oldCodeString, newPhoneStr, newCodeString, new SecurityUtils.UserOperateCallback() {
                        @Override
                        public void onSuccess() {

                            message.obj = "重新绑定手机成功";
                            showToast.sendMessage(message);
                            RebindPhoneActivity.this.finish();
                        }

                        @Override
                        public void onFail(String msg) {

                            try {
                                userInfo = SecurityUtils.getUserInfo();
                                if (userInfo.isPinHasLock()) {
                                    message.obj = "PIN码错误次数达到上限,钱包已被锁定";
                                    showToast.sendMessage(message);
                                    Intent intent = new Intent(RebindPhoneActivity.this, UnlockActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    return;
                                }
                            } catch (SecurityErrorException e1) {
                                e1.printStackTrace();
                            }
                            Message message = new Message();
                            message.obj = "验证码或PIN码错误";
                            showToast.sendMessage(message);
                        }
                    });
                } catch (SecurityErrorException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


}
