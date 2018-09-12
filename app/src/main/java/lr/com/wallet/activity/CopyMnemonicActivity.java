package lr.com.wallet.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import lr.com.wallet.R;

/**
 * Created by DT0814 on 2018/8/24.
 */

public class CopyMnemonicActivity extends SecurityActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.copy_mnemonic_layout);
        String mne = getIntent().getStringExtra("mne");
        if (null == mne || mne.trim().equals("")) {
            Log.e("CopyPrvActivityErr", "noMne");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CopyMnemonicActivity.this);
            View dangerView = getLayoutInflater().inflate(R.layout.danger_msg_layout, null);
            builder.setView(dangerView);
            AlertDialog show = builder.show();
            show.setCancelable(false);
            ((TextView) dangerView.findViewById(R.id.dangerMsgText)).setText(R.string.mnemonicDangerMsg);
            dangerView.findViewById(R.id.dangerMsgBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show.dismiss();
                }
            });

            TextView prvText = findViewById(R.id.MnemonicText);
            prvText.setText(mne);
            findViewById(R.id.copyMnemonicPerBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CopyMnemonicActivity.this.finish();
                }
            });
            Button copyBut = findViewById(R.id.copyBut);
            findViewById(R.id.copyBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipData mClipData;
                    ClipboardManager clipManager;
                    clipManager = (ClipboardManager) CopyMnemonicActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    mClipData = ClipData.newPlainText("Label", mne);
                    clipManager.setPrimaryClip(mClipData);
                    Toast.makeText(CopyMnemonicActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                    copyBut.setText("已复制");
                }
            });
        }

    }
}
