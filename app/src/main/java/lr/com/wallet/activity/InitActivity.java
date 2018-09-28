package lr.com.wallet.activity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityErrorException;
import com.hunter.wallet.service.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lr.com.wallet.R;
import lr.com.wallet.adapter.AreaCodeAdapter;
import lr.com.wallet.pojo.AreaCodePojo;

/**
 * Created by dt0814 on 2018/7/14.
 */

public class InitActivity extends Activity implements View.OnClickListener {
    private EditText phone;
    private Button querenBut;
    private Button getCode;
    private String areaCodeStr = "+86-";
    private EditText code;
    private EditText pin;
    private String phoneStr;
    private TextView areaCode;
    private EditText rePin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_wallet_layout);
        phone = findViewById(R.id.phone);
        code = findViewById(R.id.code);
        pin = findViewById(R.id.pin);
        rePin = findViewById(R.id.rePin);
        areaCode = findViewById(R.id.areaCode);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pinStr = pin.getText().toString();
                String rePinStr = rePin.getText().toString();
                if (pinStr.length() != 0 || rePinStr.length() != 0) {
                    querenBut.setBackgroundResource(R.drawable.fillet_fill_blue_on);
                } else {
                    querenBut.setBackgroundResource(R.drawable.fillet_fill_blue_off);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        pin.addTextChangedListener(textWatcher);
        rePin.addTextChangedListener(textWatcher);
        getCode = findViewById(R.id.getCode);
        getCode.setOnClickListener(this);
        querenBut = findViewById(R.id.querenBut);
        querenBut.setOnClickListener(this);
        findViewById(R.id.selectAreaCode).setOnClickListener(this);
    }


    Handler updateGetCodeBut = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getCode.setText((String) msg.obj);
            if (msg.arg1 == 0) {
                getCode.setEnabled(false);
                getCode.setBackgroundResource(R.drawable.fillet_fill_jinse_off);
            } else {
                getCode.setEnabled(true);
                getCode.setBackgroundResource(R.drawable.fillet_fill_jinse_on);
            }
        }
    };
    Handler showToast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(InitActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };
    int time = 30;

    private void showdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InitActivity.this);
        View areaView = getLayoutInflater().inflate(R.layout.select_area_code_dialog, null);
        ListView listView = areaView.findViewById(R.id.AreaCodeList);
        List<AreaCodePojo> data = new ArrayList();
        data.add(new AreaCodePojo("中国", "+86"));
        data.add(new AreaCodePojo("中国台湾", "+886"));
        data.add(new AreaCodePojo("中国香港", "+852"));
        data.add(new AreaCodePojo("美国", "+1"));
        AreaCodeAdapter adapter = new AreaCodeAdapter(InitActivity.this, R.layout.select_area_code_item, data);
        listView.setAdapter(adapter);
        builder.setView(areaView);
        AlertDialog show = builder.show();
        show.setCancelable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AreaCodePojo areaCodePojo = data.get(position);
                areaCodeStr = areaCodePojo.getNumber() + "-";
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectAreaCode:
                showdialog();
                break;
            case R.id.getCode:
                phoneStr = (areaCodeStr + phone.getText().toString()).trim();
                if (phoneStr.length() != 15) {
                    Toast.makeText(InitActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                getCode.setEnabled(false);
                getCode.setText("重新获取(" + time + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                if (--time > 0) {
                                    msg.obj = "重新获取(" + time + ")";
                                    msg.arg1 = 0;
                                    updateGetCodeBut.sendMessage(msg);
                                } else {
                                    time = 90;
                                    this.cancel();
                                    msg.obj = "重新获取";
                                    msg.arg1 = 1;
                                    updateGetCodeBut.sendMessage(msg);
                                }
                            }
                        }, 1000, 1000);
                    }
                }).start();

                SecurityUtils.sendAuthcode(phoneStr, new SecurityUtils.UserOperateCallback() {
                    @Override
                    public void onSuccess() {
                        Message message = new Message();
                        message.obj = "验证码发送成功,请注意查收";
                        showToast.sendMessage(message);
                    }

                    @Override
                    public void onFail(String msg) {
                        Log.i("init", "验证码发送失败");
                        Message showMsg = new Message();
                        showMsg.obj = "验证码发送失败";
                        showToast.sendMessage(showMsg);
                        time = 90;
                        Message message = new Message();
                        message.obj = "重新获取";
                        message.arg1 = 1;
                        updateGetCodeBut.sendMessage(message);
                    }
                });
                break;
            case R.id.querenBut:
                String pinStr = pin.getText().toString();
                String rePinStr = rePin.getText().toString();
                if (pinStr.length() != 6 || rePinStr.length() != 6) {
                    Toast.makeText(InitActivity.this, "PIN码长度应为6位", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!pinStr.equals(rePinStr)) {
                    Toast.makeText(InitActivity.this, "两次输入PIN码不一致", Toast.LENGTH_SHORT).show();
                    break;
                }
                String codeStr = code.getText().toString();
                if (null == codeStr || codeStr.trim().length() != 6) {
                    Toast.makeText(InitActivity.this, "验证码为6位数字", Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    if (null == phoneStr || phoneStr.length() != 15) {
                        break;
                    }
                    SecurityUtils.userInit(pinStr, phoneStr, codeStr, new SecurityUtils.UserOperateCallback() {
                        @Override
                        public void onSuccess() {
                            startActivity(new Intent(InitActivity.this, MainFragmentActivity.class));
                            InitActivity.this.finish();
                        }

                        @Override
                        public void onFail(String msg) {
                            Message message = new Message();
                            message.obj = "验证码错误";
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
