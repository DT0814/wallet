package lr.com.wallet.activity.fragment.info;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityErrorException;
import com.hunter.wallet.service.SecurityUtils;

import lr.com.wallet.R;

/**
 * Created by DT0814 on 2018/9/19.
 */

public class UpdatePinActivity extends Activity implements View.OnClickListener {
    private Button updateBut;
    private EditText oldPin;
    private EditText newPin;
    private EditText reNewPin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_info_fragment_update_pin_layout);
        findViewById(R.id.updatePinPreBut).setOnClickListener(this);
        updateBut = findViewById(R.id.updateBut);
        updateBut.setOnClickListener(this);
        oldPin = findViewById(R.id.oldPin);
        newPin = findViewById(R.id.newPin);
        reNewPin = findViewById(R.id.reNewPin);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newPinStr = newPin.getText().toString();
                String reNewPinStr = reNewPin.getText().toString();
                if (newPinStr.length() == 6 && reNewPinStr.length() == 6 && reNewPinStr.equals(newPinStr)) {
                    updateBut.setEnabled(true);
                    updateBut.setBackgroundResource(R.drawable.fillet_fill_blue);
                } else {
                    updateBut.setBackgroundResource(R.drawable.fillet_fill_off_blue);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        newPin.addTextChangedListener(watcher);
        reNewPin.addTextChangedListener(watcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updatePinPreBut:
                UpdatePinActivity.this.finish();
                break;
            case R.id.updateBut:
                try {
                    if (oldPin.getText().toString().length() != 6) {
                        Toast.makeText(UpdatePinActivity.this, "旧PIN码格式错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SecurityUtils.changePin(oldPin.getText().toString(), newPin.getText().toString());
                    Toast.makeText(UpdatePinActivity.this, "PIN码修改成功", Toast.LENGTH_SHORT).show();
                    UpdatePinActivity.this.finish();
                } catch (SecurityErrorException e) {
                    e.printStackTrace();
                    Toast.makeText(UpdatePinActivity.this, "旧PIN码错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
