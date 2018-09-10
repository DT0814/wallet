package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityUtils;
import com.hunter.wallet.service.SecurityErrorException;

import lr.com.wallet.R;

/**
 * Created by DT0814 on 2018/8/23.
 */

public class UpdatePassActivity extends Activity {
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
            EditText newPassword = findViewById(R.id.newPassword);
            EditText rePassword = findViewById(R.id.rePassword);
            findViewById(R.id.updatePreBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdatePassActivity.this.finish();
                }
            });
            findViewById(R.id.updatePassBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
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
            });
        }

    }
}
