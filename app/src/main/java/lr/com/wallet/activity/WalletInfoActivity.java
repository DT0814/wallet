package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityErrorException;
import com.hunter.wallet.service.SecurityUtils;


import org.web3j.utils.Numeric;

import lr.com.wallet.R;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.JsonUtils;


/**
 * Created by DT0814 on 2018/8/22.
 */

public class WalletInfoActivity extends Activity implements View.OnClickListener {
    private ETHCacheWallet ethCacheWallet;
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
        ethCacheWallet = JsonUtils.jsonToPojo(getIntent().getStringExtra("wallet"), ETHCacheWallet.class);
        findViewById(R.id.copyPrvkeyBut).setOnClickListener(this);
        findViewById(R.id.copyKeyStoreBut).setOnClickListener(this);
        findViewById(R.id.copyMnemonicBut).setOnClickListener(this);
        findViewById(R.id.updatePassBut).setOnClickListener(this);
        findViewById(R.id.deleteWalletBut).setOnClickListener(this);
        findViewById(R.id.saveBut).setOnClickListener(this);
        findViewById(R.id.walletInfoPreBut).setOnClickListener(this);
        TextView ethNum = findViewById(R.id.ethNum);
        TextView walletName = findViewById(R.id.walletName);
        TextView addressText = findViewById(R.id.addressText);
        nameEdit = findViewById(R.id.nameEdit);
        ImageView touXiangImg = findViewById(R.id.touXiangImg);
        switch (ethCacheWallet.getId().intValue() % 2) {
            case 1:
                touXiangImg.setImageResource(R.drawable.touxiang2);
                break;
            case 0:
                touXiangImg.setImageResource(R.drawable.touxiang);
                break;
        }

        nameEdit.setText(ethCacheWallet.getName());
        walletName.setText(ethCacheWallet.getName());
        if (null == ethCacheWallet.getNum()) {
            ethNum.setText("0");
        } else {
            ethNum.setText(ethCacheWallet.getNum().toString());
        }

        addressText.setText(ethCacheWallet.getAddress());

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
                checkEnv(COPYPRVKEYBUTSTATE);

                break;
            case R.id.copyKeyStoreBut:
                checkEnv(COPYKEYSTOREBUTSTATE);

                break;
            case R.id.copyMnemonicBut:
                checkEnv(COPYMNEMONICBUTSTATE);

                break;
            case R.id.updatePassBut:
                Intent toUpdateIntent = new Intent(WalletInfoActivity.this, UpdatePassActivity.class);
                toUpdateIntent.putExtra("walletId", ethCacheWallet.getId().intValue());
                startActivity(toUpdateIntent);
                break;
            case R.id.saveBut:
                String text = nameEdit.getText().toString();
                if (!text.equals(ethCacheWallet.getName())) {
                    try {
                        ethCacheWallet.setName(text);
                        SecurityUtils.changeName(ethCacheWallet.getId().intValue(), ethCacheWallet.getName());
                        CacheWalletDao.update(ethCacheWallet);
                    } catch (SecurityErrorException e) {
                        e.printStackTrace();
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(WalletInfoActivity.this);
                View successView = getLayoutInflater().inflate(R.layout.success_layout, null);
                builder.setView(successView);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCancelable(false);
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
                            SecurityUtils.deleteWallet(ethCacheWallet.getId().intValue(), passWord);
                            CacheWalletDao.deleteWallet(ethCacheWallet);
                            Toast.makeText(WalletInfoActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                            Intent toWalletIntent = new Intent(WalletInfoActivity.this, MainFragmentActivity.class);
                            toWalletIntent.putExtra("position", 1);
                            startActivity(toWalletIntent);
                            inPassDialog.dismiss();
                            WalletInfoActivity.this.finish();
                        } catch (SecurityErrorException e) {
                            if (e.getErrorCode() == SecurityErrorException.ERROR_PASSWORD_WRONG) {
                                Toast.makeText(inPass.getContext(), "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                            } else if (e.getErrorCode() == SecurityErrorException.ERROR_WALLET_CANOT_FOUND) {
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
                            result = SecurityUtils.getKeystore(ethCacheWallet.getId().intValue(), passWord);
                            Intent keyIntent = new Intent(WalletInfoActivity.this, CopyKeyStoreActivity.class);
                            keyIntent.putExtra("key", result);
                            startActivity(keyIntent);
                            break;
                        case COPYMNEMONICBUTSTATE:
                            String mnemonic = SecurityUtils.getMnemonic(ethCacheWallet.getId().intValue(), passWord);
                            result = mnemonic.trim();
                            Log.i("mnemonic", mnemonic + "      " + result);
                            if (result.equals("")) {
                                Toast.makeText(WalletInfoActivity.this
                                        , "您未通过本app创建钱包,或未使用助记词导入钱包到本app,所以本app无法导出当前钱包助记词"
                                        , Toast.LENGTH_LONG).show();
                                return;
                            }
                            Intent mnIntent = new Intent(WalletInfoActivity.this, CopyMnemonicActivity.class);
                            mnIntent.putExtra("mne", result);
                            startActivity(mnIntent);
                            break;
                        case COPYPRVKEYBUTSTATE:
                            result = Numeric.toHexString(SecurityUtils.getPrikey(ethCacheWallet.getId().intValue(), passWord));
                            Intent prvIntent = new Intent(WalletInfoActivity.this, CopyPrvActivity.class);
                            prvIntent.putExtra("prv", result);
                            startActivity(prvIntent);
                            break;
                    }
                    inPassDialog.dismiss();
                } catch (SecurityErrorException e) {
                    Log.i("SecurityErrorException", e.getErrorCode() + "");
                    Log.i("SecurityErrorException", SecurityErrorException.ERROR_PASSWORD_WRONG + "");
                    Log.i("SecurityErrorException", (e.getErrorCode() == SecurityErrorException.ERROR_PASSWORD_WRONG) + "");
                    if (e.getErrorCode() == SecurityErrorException.ERROR_PASSWORD_WRONG) {
                        Toast.makeText(inPass.getContext(), "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void checkEnv(int state) {
        SecurityUtils.checkEnv(WalletInfoActivity.this, new SecurityUtils.CheckEnvCallback() {
            @Override
            public void onSuccess() {
                copyFunc(state);
            }

            @Override
            public void onFail() {

            }
        });
//        AlertDialog.Builder reminderBuilder = new AlertDialog.Builder(WalletInfoActivity.this);
//        View reminderView = getLayoutInflater().inflate(R.layout.reminder_layout, null);
//        reminderBuilder.setView(reminderView);
//        AlertDialog show = reminderBuilder.show();
//        show.setCancelable(false);
//        reminderView.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                show.dismiss();
//            }
//        });
//        reminderView.findViewById(R.id.agreeBut).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SecurityUtils.shutdownOtherApp(WalletInfoActivity.this);
//                show.dismiss();
//                copyFunc(state);
//            }
//        });
    }
}
