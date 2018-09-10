package lr.com.wallet.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.ChooseMnemonicAdapter;
import lr.com.wallet.adapter.MnemonicAdapter;
import lr.com.wallet.utils.AutoLineFeedLayoutManager;

public class BackupMnemonicActivity extends SecurityActivity {
    private String mnemonic;
    private ChooseMnemonicAdapter chooseMnemonicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_mnemonic_activity);

        mnemonic = getIntent().getStringExtra("mnemonic");
        final String[] mnemonicArray = mnemonic.split(" ");
        List<String> list = new ArrayList<>(Arrays.asList(mnemonicArray));
        Collections.shuffle(list);
        Button mnemonic_confirm = findViewById(R.id.mnemonic_confirm);
        chooseMnemonicAdapter = new ChooseMnemonicAdapter(new ArrayList<String>(), mnemonic_confirm);

        MnemonicAdapter adapter = new MnemonicAdapter(list, mnemonic_confirm);

        RecyclerView chooseRecyclerView = findViewById(R.id.mnemonic_choose_recycler_view);
        AutoLineFeedLayoutManager layoutManager = new AutoLineFeedLayoutManager();
        chooseRecyclerView.setLayoutManager(layoutManager);
        chooseMnemonicAdapter.setMnemonicAdapter(adapter);
        chooseRecyclerView.setAdapter(chooseMnemonicAdapter);

        RecyclerView recyclerView = findViewById(R.id.mnemonic_recycler_view);
        AutoLineFeedLayoutManager layoutManager2 = new AutoLineFeedLayoutManager();
        recyclerView.setLayoutManager(layoutManager2);

        adapter.setChooseMnemonicAdapter(chooseMnemonicAdapter);
        recyclerView.setAdapter(adapter);
        findViewById(R.id.mnemonic_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BackupMnemonicActivity.this, CreateShowMnemonicActivity.class);
                intent.putExtra("mnemonic", mnemonic.trim());
                startActivity(intent);
                BackupMnemonicActivity.this.finish();
            }
        });
        mnemonic_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> stringList = new ArrayList<>();
                stringList = chooseMnemonicAdapter.getMyList();
                String chooseMnemonic = Joiner.on(" ").join(stringList);
                if (mnemonic.trim().equals(chooseMnemonic.trim())) {
                    //消息提示
                    AlertDialog alertDialog = new AlertDialog.Builder(BackupMnemonicActivity.this)
                            .setTitle("提示:")//设置对话框的标题
                            .setMessage("备份成功")//设置对话框的内容
                            //设置对话框的按钮
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(BackupMnemonicActivity.this, MainFragmentActivity.class));
                                }
                            }).create();
                    alertDialog.show();
                } else {
                    //消息提示
                    AlertDialog alertDialog = new AlertDialog.Builder(BackupMnemonicActivity.this)
                            .setTitle("提示:")//设置对话框的标题
                            .setMessage("助记词顺序错误，请检查。")//设置对话框的内容
                            //设置对话框的按钮
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    alertDialog.show();
                }
            }
        });
    }
}
