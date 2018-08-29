package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
        findViewById(R.id.walletInfoPreBut).setOnClickListener(this);
        ethNum = findViewById(R.id.ethNum);
        walletName = findViewById(R.id.walletName);
        addressText = findViewById(R.id.addressText);
        nameEdit = findViewById(R.id.nameEdit);


        Intent intent = getIntent();
        String walletStr = intent.getStringExtra("wallet");
        ethWallet = JsonUtils.jsonToPojo(walletStr, ETHWallet.class);

        nameEdit.setText(ethWallet.getName());
        walletName.setText(ethWallet.getName());
        if (null == ethWallet.getNum()) {
            ethNum.setText("0");
        } else {
            ethNum.setText(ethWallet.getNum().toString());
        }

        addressText.setText(ethWallet.getAddress());

    }

    @Override
    public void onClick(View v) {
        if (null == alertbBuilder) {
            alertbBuilder = new AlertDialog.Builder(WalletInfoActivity.this);
        }
        switch (v.getId()) {
            case R.id.walletInfoPreBut:
                this.finish();
                break;
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
                Intent toUpdateIntent = new Intent(WalletInfoActivity.this, UpdatePassActivity.class);
                toUpdateIntent.putExtra("walletId", ethWallet.getId().intValue());
                startActivity(toUpdateIntent);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(WalletInfoActivity.this);
                View successView = getLayoutInflater().inflate(R.layout.success_layout, null);
                builder.setView(successView);
                AlertDialog dialog = builder.create();
                dialog.show();
                successView.findViewById(R.id.quedingBut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WalletInfoActivity.this, MainFragmentActivity.class);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                        WalletInfoActivity.this.finish();
                        dialog.dismiss();
                    }
                });

                break;
            case R.id.deleteWalletBut:
                View inPass = inflater.inflate(R.layout.input_pwd_layout, null);
                alertbBuilder.setView(inPass);
                AlertDialog inPassDialog = alertbBuilder.show();
                inPass.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inPassDialog.dismiss();
                    }
                });
                inPass.findViewById(R.id.inputPassBut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = inPass.findViewById(R.id.inPwdEdit);
                        String passWord = editText.getText().toString();
                        try {
                            SecurityService.deleteWallet(ethWallet.getId().intValue(), passWord);
                            WalletDao.deleteWallet(ethWallet);
                            Toast.makeText(WalletInfoActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                            Intent toWalletIntent = new Intent(WalletInfoActivity.this, MainFragmentActivity.class);
                            toWalletIntent.putExtra("position", 1);
                            startActivity(toWalletIntent);
                            inPassDialog.dismiss();
                            WalletInfoActivity.this.finish();
                        } catch (TeeErrorException e) {
                            if (e.getErrorCode() == TeeErrorException.TEE_ERROR_PASSWORD_WRONG) {
                                Toast.makeText(inPass.getContext(), "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                            } else if (e.getErrorCode() == TeeErrorException.TEE_ERROR_WALLET_CANOT_FOUND) {
                                Toast.makeText(inPass.getContext(), "钱包不存在", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    }

                });
                break;
        }
    }

    private void copyFunc(int state) {
        View inPass = inflater.inflate(R.layout.input_pwd_layout, null);
        alertbBuilder.setView(inPass);
        AlertDialog inPassDialog = alertbBuilder.show();
        inPass.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inPassDialog.dismiss();
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
                            Intent keyIntent = new Intent(WalletInfoActivity.this, CopyKeyStoreActivity.class);
                            keyIntent.putExtra("key", result);
                            startActivity(keyIntent);
                            break;
                        case COPYMNEMONICBUTSTATE:
                            StringBuffer sb = new StringBuffer();
                            SecurityService.getMnemonic(ethWallet.getId().intValue(), passWord).forEach((s) -> {
                                sb.append(s + " ");
                            });
                            result = sb.toString().trim();
                            Intent mnIntent = new Intent(WalletInfoActivity.this, CopyMnemonicActivity.class);
                            mnIntent.putExtra("mne", result);
                            startActivity(mnIntent);
                            break;
                        case COPYPRVKEYBUTSTATE:
                            result = Numeric.toHexString(SecurityService.getPrikey(ethWallet.getId().intValue(), passWord));
                            Intent prvIntent = new Intent(WalletInfoActivity.this, CopyPrvActivity.class);
                            prvIntent.putExtra("prv", result);
                            startActivity(prvIntent);
                            break;
                    }
                    inPassDialog.dismiss();
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
