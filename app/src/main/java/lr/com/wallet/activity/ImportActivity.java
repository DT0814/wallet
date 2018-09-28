package lr.com.wallet.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.fragment.KeyStoreImportFragment;
import lr.com.wallet.activity.fragment.MnemonicImportFragment;
import lr.com.wallet.activity.fragment.PrevateImportFragment;
import lr.com.wallet.utils.AddressEncoder;
import lr.com.wallet.utils.ImportUpdateInterface;
import lr.com.wallet.utils.ReminderUtils;
import lr.com.wallet.utils.Type;

/**
 * Created by dt0814 on 2018/7/14.
 */

public class ImportActivity extends SecurityFragmentActivity implements View.OnClickListener {
    private List<android.support.v4.app.Fragment> fragments;
    private List<ImportUpdateInterface> fragmentsUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_wallet_layout);
        KeyStoreImportFragment keyStoreImportFragment = new KeyStoreImportFragment();
        MnemonicImportFragment mnemonicImportFragment = new MnemonicImportFragment();
        PrevateImportFragment prevateImportFragment = new PrevateImportFragment();
        findViewById(R.id.saoyisao).setOnClickListener(this);
        fragments = new ArrayList<>();
        fragments.add(keyStoreImportFragment);
        fragments.add(prevateImportFragment);
        fragments.add(mnemonicImportFragment);

        fragmentsUpdate = new ArrayList<>();
        fragmentsUpdate.add(keyStoreImportFragment);
        fragmentsUpdate.add(prevateImportFragment);
        fragmentsUpdate.add(mnemonicImportFragment);


        onTabSelected(0);
        RadioGroup rd = findViewById(R.id.radioGroup);
        findViewById(R.id.importPreBut).setOnClickListener(this);

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

    /**
     * 扫过二维码回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            Log.i("update", result);
            try {
                for (ImportUpdateInterface importUpdateInterface : fragmentsUpdate) {
                    importUpdateInterface.update(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == Type.CONSTACT_RESULT_CODE && null != data) {
            Toast.makeText(ImportActivity.this, "二维码解析失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    startActivityForResult(new Intent(ImportActivity.this, CaptureActivity.class), 0);
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(ImportActivity.this, "请手动打开相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importPreBut:
                ImportActivity.this.finish();
                break;
            case R.id.saoyisao:
                //判断摄像头权限
                if (ContextCompat.checkSelfPermission(ImportActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //先判断有没有权限 ，没有就在这里进行权限的申请
                    ActivityCompat.requestPermissions(ImportActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    Log.i("update", "**********&&**************");
                    startActivityForResult(new Intent(ImportActivity.this, CaptureActivity.class), 0);
                }
                break;

        }
    }
}
