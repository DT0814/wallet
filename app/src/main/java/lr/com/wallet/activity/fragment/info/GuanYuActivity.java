package lr.com.wallet.activity.fragment.info;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;

import lr.com.wallet.R;

/**
 * Created by DT0814 on 2018/8/27.
 */

public class GuanYuActivity extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_info_fragment_guanyu_layout);
        findViewById(R.id.guanyuPreBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuanYuActivity.this.finish();
            }
        });
    }
}
