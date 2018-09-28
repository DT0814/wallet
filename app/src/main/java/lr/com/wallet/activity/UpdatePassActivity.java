package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityUtils;
import com.hunter.wallet.service.SecurityErrorException;

import lr.com.wallet.R;
import lr.com.wallet.utils.PassUtils;

/**
 * Created by DT0814 on 2018/8/23.
 */

public class UpdatePassActivity extends Activity {
    private EditText newPassword;
    private EditText rePassword;
    private Button updatePassBut;
    private ImageView newPasswordIcon;
    private ImageView rePasswordIcon;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_pass_layout);
        Intent intent = getIntent();
        int walletId = intent.getIntExtra("walletId", -1);
        if (walletId == -1) {
            Log.e("UpdatePassActivity", "walletId接收失败");
            UpdatePassActivity.this.finish();
        } else {
            EditText oldPassword = findViewById(R.id.oldPassword);

            newPassword = findViewById(R.id.newPassword);
            rePassword = findViewById(R.id.rePassword);
            newPasswordIcon = findViewById(R.id.newPasswordIcon);
            rePasswordIcon = findViewById(R.id.rePasswordIcon);
            findViewById(R.id.updatePreBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdatePassActivity.this.finish();
                }
            });
            newPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String repassStr = rePassword.getText().toString();
                    String passStr = newPassword.getText().toString();
                    if (PassUtils.checkPass(passStr)) {
                        newPasswordIcon.setImageResource(R.drawable.dui_on);
                    } else {
                        newPasswordIcon.setImageResource(R.drawable.dui_off);
                    }
                    if (repassStr.equals(passStr)) {
                        rePasswordIcon.setImageResource(R.drawable.dui_on);
                        updatePassBut.setEnabled(true);
                        updatePassBut.setBackgroundResource(R.drawable.fillet_fill_blue_on);
                    } else {
                        updatePassBut.setEnabled(false);
                        updatePassBut.setBackgroundResource(R.drawable.fillet_fill_blue_off);
                        rePasswordIcon.setImageResource(R.drawable.dui_off);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            rePassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String repassStr = rePassword.getText().toString();
                    String passStr = newPassword.getText().toString();
                    if (repassStr.equals(passStr)) {
                        rePasswordIcon.setImageResource(R.drawable.dui_on);
                        updatePassBut.setEnabled(true);
                        updatePassBut.setBackgroundResource(R.drawable.fillet_fill_blue_on);
                    } else {
                        updatePassBut.setEnabled(false);
                        updatePassBut.setBackgroundResource(R.drawable.fillet_fill_blue_off);
                        rePasswordIcon.setImageResource(R.drawable.dui_off);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            updatePassBut = findViewById(R.id.updatePassBut);
            updatePassBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String passwordStr = newPassword.getText().toString();
                    final String rePasswordStr = rePassword.getText().toString();
                    final String oldPasswordStr = oldPassword.getText().toString();
                    if (null == oldPasswordStr || oldPasswordStr.length() < 6
                            || null == rePasswordStr || rePasswordStr.length() < 6
                            || null == passwordStr || passwordStr.length() < 6) {
                        Toast.makeText(UpdatePassActivity.this, "密码长度不得小于6位", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!passwordStr.equals(rePasswordStr)) {
                        Toast.makeText(UpdatePassActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!PassUtils.checkPass(passwordStr)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePassActivity.this);
                        View daView = getLayoutInflater().inflate(R.layout.danger_pwd_dialog, null);
                        builder.setView(daView);
                        AlertDialog show = builder.show();
                        daView.findViewById(R.id.confirmBut).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    SecurityUtils.changePassword(walletId, oldPasswordStr, passwordStr);
                                    Toast.makeText(UpdatePassActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    UpdatePassActivity.this.finish();
                                } catch (SecurityErrorException e) {
                                    if (e.getErrorCode() == SecurityErrorException.ERROR_PASSWORD_WRONG) {
                                        Toast.makeText(UpdatePassActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                    e.printStackTrace();
                                }
                                show.dismiss();
                            }
                        });
                        daView.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show.dismiss();
                            }
                        });
                    } else {
                        try {
                            SecurityUtils.changePassword(walletId, oldPasswordStr, passwordStr);
                            Toast.makeText(UpdatePassActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            UpdatePassActivity.this.finish();
                        } catch (SecurityErrorException e) {
                            if (e.getErrorCode() == SecurityErrorException.ERROR_PASSWORD_WRONG) {
                                Toast.makeText(UpdatePassActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
                            }
                            e.printStackTrace();
                        }
                    }
                }

            });
        }


    }
}
