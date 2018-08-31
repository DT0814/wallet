package lr.com.wallet.activity.fragment.info;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.IOException;

import lr.com.wallet.R;
import lr.com.wallet.activity.TxActivity;
import lr.com.wallet.dao.ContactsDao;
import lr.com.wallet.pojo.Contacts;
import lr.com.wallet.utils.AddressEncoder;
import lr.com.wallet.utils.Type;

/**
 * Created by DT0814 on 2018/8/31.
 */

public class AddContactsActivity extends Activity {
    EditText address;
    EditText contactsName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_info_fragment_contacts_add_layout);
        address = findViewById(R.id.address);
        contactsName = findViewById(R.id.contactsName);
        findViewById(R.id.addContactsPreBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddContactsActivity.this.finish();
            }
        });
        findViewById(R.id.addContactsBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactsNameStr = contactsName.getText().toString().trim();
                String addressStr = address.getText().toString().trim();
                if (contactsNameStr.equals("")) {
                    Toast.makeText(AddContactsActivity.this, "联系人不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addressStr.equals("")) {
                    Toast.makeText(AddContactsActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Contacts contacts = new Contacts(contactsNameStr, addressStr, Type.ETH_TYPE);
                Contacts write = ContactsDao.write(contacts);
                if (null != write) {
                    Toast.makeText(AddContactsActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddContactsActivity.this, ContactsActivity.class));
                    AddContactsActivity.this.finish();
                } else {
                    Toast.makeText(AddContactsActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.saoyisao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddContactsActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //先判断有没有权限 ，没有就在这里进行权限的申请
                    ActivityCompat.requestPermissions(AddContactsActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    startActivityForResult(new Intent(AddContactsActivity.this, CaptureActivity.class), 0);
                }
            }
        });
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
            try {
                if (result.startsWith("0x") || result.startsWith("0X")) {
                    address.setText(result);
                } else if (result.startsWith("iban:XE") || result.startsWith("IBAN:XE")) {
                    address.setText(AddressEncoder.decodeICAP(result).getAddress());
                } else if (result.startsWith("iban:") || result.startsWith("IBAN:")) {
                    address.setText(AddressEncoder.decodeLegacyLunary(result).getAddress());
                } else if (result.startsWith("ethereum:") || result.startsWith("ETHEREUM:")) {
                    address.setText(AddressEncoder.decodeERC(result).getAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(AddContactsActivity.this, "二维码解析失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
