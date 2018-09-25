package lr.com.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;


import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.fragment.KeyStoreImportFragment;
import lr.com.wallet.activity.fragment.MnemonicImportFragment;
import lr.com.wallet.activity.fragment.PrevateImportFragment;
import lr.com.wallet.utils.ReminderUtils;

/**
 * Created by dt0814 on 2018/7/14.
 */

public class ImportActivity extends SecurityFragmentActivity {
    private List<android.support.v4.app.Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_wallet_layout);
        KeyStoreImportFragment keyStoreImportFragment = new KeyStoreImportFragment();
        MnemonicImportFragment mnemonicImportFragment = new MnemonicImportFragment();
        PrevateImportFragment prevateImportFragment = new PrevateImportFragment();
        fragments = new ArrayList<>();
        fragments.add(keyStoreImportFragment);
        fragments.add(prevateImportFragment);
        fragments.add(mnemonicImportFragment);
        onTabSelected(0);
        RadioGroup rd = findViewById(R.id.radioGroup);
        findViewById(R.id.importPreBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        /*        Intent intent = new Intent(ImportActivity.this, MainFragmentActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);*/
                ImportActivity.this.finish();
            }
        });

        rd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                switch (checkId) {
                    case R.id.keyStoreRadio:
                        onTabSelected(0);
                        break;
                    case R.id.prvRadio:
                        onTabSelected(1);
                        break;
                    case R.id.WordRadio:
                        onTabSelected(2);
                        break;
                }
            }
        });
//        ReminderUtils.showReminder(ImportActivity.this);
    }

    //点击item时跳转不同的碎片

    public void onTabSelected(int position) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();

        if (position == 0) {
            if (!manager.getFragments().contains(fragments.get(0))) {
                ft.add(R.id.importFrame, fragments.get(0));
            }
            ft.hide(fragments.get(1));
            ft.hide(fragments.get(2));
            ft.show(fragments.get(0));
            ft.commit();
        }
        if (position == 1) {
            if (!manager.getFragments().contains(fragments.get(1))) {
                ft.add(R.id.importFrame, fragments.get(1));
            }
            ft.hide(fragments.get(0));
            ft.hide(fragments.get(2));
            ft.show(fragments.get(1));
            ft.commit();
        }
        if (position == 2) {
            if (!manager.getFragments().contains(fragments.get(2))) {
                ft.add(R.id.importFrame, fragments.get(2));
            }
            ft.hide(fragments.get(0));
            ft.hide(fragments.get(1));
            ft.show(fragments.get(2));
            ft.commit();
        }
    }
}
