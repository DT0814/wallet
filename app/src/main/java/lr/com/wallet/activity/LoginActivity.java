package lr.com.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lr.com.wallet.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button but = (Button) this.findViewById(R.id.login_button);
        final Button CreBut = (Button) this.findViewById(R.id.createWallet);
        final TextView acc = this.findViewById(R.id.account);
        final TextView pass = this.findViewById(R.id.password);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
                intent.putExtra("acc", acc.getText().toString());
                startActivity(intent);
            }
        });
        CreBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CreateWalletActivity.class);
                startActivity(intent);
            }
        });
    }
}
