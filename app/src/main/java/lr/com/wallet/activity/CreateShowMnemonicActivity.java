package lr.com.wallet.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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

public class CreateShowMnemonicActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_show_mnemonic_layout);
        String mnemonic = getIntent().getStringExtra("mnemonic");
        if (null == mnemonic || mnemonic.trim().equals("")) {
            Log.e("CopyPrvActivityErr", "noMne");
        } else {
            TextView prvText = findViewById(R.id.MnemonicText);
            prvText.setText(mnemonic);
            findViewById(R.id.copyMnemonicPerBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CreateShowMnemonicActivity.this, MainFragmentActivity.class);
                    intent.putExtra("position", 1);
                    startActivity(intent);
                    CreateShowMnemonicActivity.this.finish();
                }
            });
            findViewById(R.id.nextBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CreateShowMnemonicActivity.this, BackupMnemonicActivity.class);
                    intent.putExtra("mnemonic", mnemonic);
                    startActivity(intent);
                }
            });
        }

    }
}
