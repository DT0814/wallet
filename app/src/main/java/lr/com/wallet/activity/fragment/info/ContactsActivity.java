package lr.com.wallet.activity.fragment.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.ContactsAdapter;
import lr.com.wallet.dao.ContactsDao;
import lr.com.wallet.pojo.Contacts;
import lr.com.wallet.utils.Type;

/**
 * Created by DT0814 on 2018/8/31.
 */

public class ContactsActivity extends Activity {
    private ListView listView;
    private boolean itemClickAble;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_info_fragment_contacts_layout);
        findViewById(R.id.contactsPreBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsActivity.this.finish();
            }
        });
        findViewById(R.id.contactsAddbut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, AddContactsActivity.class);
                intent.putExtra("type", Type.ETH_TYPE);
                startActivity(intent);
                ContactsActivity.this.finish();
            }
        });
        Intent intent = getIntent();
        itemClickAble = intent.getBooleanExtra("itemClickAble", false);
        listView = findViewById(R.id.listView);

        init();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            listView.setAdapter((ListAdapter) msg.obj);
            if (itemClickAble) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Contacts contacts = (Contacts) parent.getItemAtPosition(position);
                        Intent intent = new Intent();
                        intent.putExtra("result", contacts.getAddress());
                        setResult(Type.CONSTACT_RESULT_CODE, intent);
                        ContactsActivity.this.finish();
                    }
                });
            }
        }
    };

    private void init() {
        List<Contacts> data = ContactsDao.getEthContacts();
        if (null == data || data.size() < 1) {
            findViewById(R.id.noContacts).setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        ContactsAdapter adapter = new ContactsAdapter(ContactsActivity.this
                , R.layout.main_info_fragment_contacts_item
                , data);
        Message message = new Message();
        message.obj = adapter;
        handler.sendMessage(message);
    }
}
