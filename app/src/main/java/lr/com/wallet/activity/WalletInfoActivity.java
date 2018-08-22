package lr.com.wallet.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityService;
import com.hunter.wallet.service.TeeErrorException;


import org.web3j.crypto.CipherException;
import org.web3j.utils.Numeric;

import java.io.IOException;

import lr.com.wallet.R;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.JsonUtils;


/**
 * Created by DT0814 on 2018/8/22.
 */

public class WalletInfoActivity extends Activity implements View.OnClickListener {
    private ETHWallet ethWallet;
    private TextView ethNum;
    private TextView walletName;
    private TextView addressText;
    private EditText nameEdit;
    LayoutInflater inflater;
    AlertDialog.Builder alertbBuilder;
    private static final int COPYPRVKEYBUTSTATE = 1;
    private static final int COPYMNEMONICBUTSTATE = 2;
    private static final int COPYKEYSTOREBUTSTATE = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_info_layout);
        inflater = getLayoutInflater();
        findViewById(R.id.copyPrvkeyBut).setOnClickListener(this);
        findViewById(R.id.copyKeyStoreBut).setOnClickListener(this);
        findViewById(R.id.copyMnemonicBut).setOnClickListener(this);
        findViewById(R.id.updatePassBut).setOnClickListener(this);
        findViewById(R.id.deleteWalletBut).setOnClickListener(this);
        findViewById(R.id.saveBut).setOnClickListener(this);
        ethNum = findViewById(R.id.ethNum);
        walletName = findViewById(R.id.walletName);
        addressText = findViewById(R.id.addressText);
        nameEdit = findViewById(R.id.nameEdit);


        Intent intent = getIntent();
        String walletStr = intent.getStringExtra("wallet");
        ethWallet = JsonUtils.jsonToPojo(walletStr, ETHWallet.class);

        nameEdit.setText(ethWallet.getName());
        walletName.setText(ethWallet.getName());
        ethNum.setText(ethWallet.getNum().toString());
        addressText.setText(ethWallet.getAddress());

    }

    @Override
    public void onClick(View v) {
        if (null == alertbBuilder) {
            alertbBuilder = new AlertDialog.Builder(WalletInfoActivity.this);
        }
        switch (v.getId()) {
            case R.id.copyPrvkeyBut:
                copyFunc(COPYPRVKEYBUTSTATE);
                break;
            case R.id.copyKeyStoreBut:
                copyFunc(COPYKEYSTOREBUTSTATE);
                break;
            case R.id.copyMnemonicBut:
                copyFunc(COPYMNEMONICBUTSTATE);
                break;
            case R.id.updatePassBut:
                break;
            case R.id.saveBut:
                String text = nameEdit.getText().toString();
                if (!text.equals(ethWallet.getName())) {
                    try {
                        ethWallet.setName(text);
                        SecurityService.changeName(ethWallet.getId().intValue(), ethWallet.getName());
                        WalletDao.update(ethWallet);
                    } catch (TeeErrorException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(WalletInfoActivity.this, MainFragmentActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);
                WalletInfoActivity.this.finish();
                break;
        }
    }

    private void copyFunc(int state) {
        View inPass = inflater.inflate(R.layout.input_pwd_layout, null);
        alertbBuilder.setView(inPass);
        AlertDialog show = alertbBuilder.show();
        inPass.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
        inPass.findViewById(R.id.inputPassBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = inPass.findViewById(R.id.inPwdEdit);
                String passWord = editText.getText().toString();
                try {
                    String result = "";
                    switch (state) {
                        case COPYKEYSTOREBUTSTATE:
                            result = SecurityService.getKeystore(ethWallet.getId().intValue(), passWord);
                            break;
                        case COPYMNEMONICBUTSTATE:
                            StringBuffer sb = new StringBuffer();
                            SecurityService.getMnemonic(ethWallet.getId().intValue(), passWord).forEach((s) -> {
                                sb.append(s + " ");
                            });
                            result = sb.toString().trim();
                            break;
                        case COPYPRVKEYBUTSTATE:
                            result = Numeric.toHexString(SecurityService.getPrikey(ethWallet.getId().intValue(), passWord));
                            break;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(WalletInfoActivity.this);
                    View view = inflater.inflate(R.layout.show_prv_me_ks_layout, null);
                    builder.setView(view);
                    TextView msg = view.findViewById(R.id.showMsg);
                    TextView dangetMsg = view.findViewById(R.id.dangerMsg);

                    msg.setText(result);
                    dangetMsg.setText(R.string.prvDangetMsg);
                    AlertDialog dialog = builder.show();
                    view.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            show.dismiss();
                        }
                    });
                    Button copyBut = view.findViewById(R.id.copyBut);
                    copyBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipData mClipData;
                            ClipboardManager clipManager;
                            clipManager = (ClipboardManager) WalletInfoActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            mClipData = ClipData.newPlainText("Label", msg.getText().toString());
                            clipManager.setPrimaryClip(mClipData);
                            Toast.makeText(inPass.getContext(), "复制成功", Toast.LENGTH_SHORT).show();
                            copyBut.setText("已复制");
                        }
                    });
                } catch (TeeErrorException e) {
                    Log.i("TeeErrorException", e.getErrorCode() + "");
                    Log.i("TeeErrorException", TeeErrorException.TEE_ERROR_PASSWORD_WRONG + "");
                    Log.i("TeeErrorException", (e.getErrorCode() == TeeErrorException.TEE_ERROR_PASSWORD_WRONG) + "");
                    if (e.getErrorCode() == TeeErrorException.TEE_ERROR_PASSWORD_WRONG) {
                        Toast.makeText(inPass.getContext(), "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                    }
                } catch (CipherException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
