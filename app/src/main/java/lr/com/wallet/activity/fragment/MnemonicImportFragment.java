package lr.com.wallet.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.base.Splitter;
import com.hunter.wallet.service.SecurityUtils;
import com.hunter.wallet.service.SecurityErrorException;

import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ConvertPojo;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.ImportUpdateInterface;
import lr.com.wallet.utils.PassUtils;

/**
 * Created by DT0814 on 2018/8/14.
 */

public class MnemonicImportFragment extends Fragment implements ImportUpdateInterface {
    private FragmentActivity activity;
    private EditText importInPut;
    private EditText passWord;
    private EditText reImportPassword;
    private EditText importWalletName;
    private ETHCacheWallet ethCacheWallet;
    private String mnemoincTypeStr = ETHWalletUtils.ETH_JAXX_TYPE;
    private ImageView importPasswordIcon;
    private ImageView reImportPasswordIcon;
    private Button importButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_mnemonic_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        Context context = activity.getBaseContext();
        AppFilePath.init(context);

        importInPut = view.findViewById(R.id.importInPut);
        passWord = view.findViewById(R.id.importPassword);
        reImportPassword = view.findViewById(R.id.reImportPassword);
        importWalletName = view.findViewById(R.id.importWalletName);
        importButton = view.findViewById(R.id.importButton);

        importPasswordIcon = view.findViewById(R.id.importPasswordIcon);
        reImportPasswordIcon = view.findViewById(R.id.reImportPasswordIcon);
        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String repassStr = reImportPassword.getText().toString();
                String passStr = passWord.getText().toString();
                if (PassUtils.checkPass(passStr)) {
                    importPasswordIcon.setImageResource(R.drawable.dui_on);
                } else {
                    importPasswordIcon.setImageResource(R.drawable.dui_off);
                }
                if (repassStr.equals(passStr)) {
                    reImportPasswordIcon.setImageResource(R.drawable.dui_on);
                    importButton.setEnabled(true);
                    importButton.setBackgroundResource(R.drawable.fillet_fill_blue_on);
                } else {
                    importButton.setEnabled(false);
                    importButton.setBackgroundResource(R.drawable.fillet_fill_blue_off);
                    reImportPasswordIcon.setImageResource(R.drawable.dui_off);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reImportPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String repassStr = reImportPassword.getText().toString();
                String passStr = passWord.getText().toString();
                if (repassStr.equals(passStr)) {
                    reImportPasswordIcon.setImageResource(R.drawable.dui_on);
                    importButton.setEnabled(true);
                    importButton.setBackgroundResource(R.drawable.fillet_fill_blue_on);
                } else {
                    importButton.setEnabled(false);
                    importButton.setBackgroundResource(R.drawable.fillet_fill_blue_off);
                    reImportPasswordIcon.setImageResource(R.drawable.dui_off);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        Spinner mnemoincType = view.findViewById(R.id.mnemoincType);
        mnemoincType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mnemoincTypeStr = ETHWalletUtils.ETH_JAXX_TYPE;
                        break;
                    case 1:
                        mnemoincTypeStr = ETHWalletUtils.ETH_LEDGER_TYPE;
                        break;
                    case 2:
                        mnemoincTypeStr = ETHWalletUtils.ETH_CUSTOM_TYPE;
                        break;
                    default:
                        mnemoincTypeStr = ETHWalletUtils.ETH_JAXX_TYPE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passString = passWord.getText().toString();
                String repassString = reImportPassword.getText().toString();
                if (passString.trim().length() <= 5) {
                    Toast.makeText(activity, "密码长度不能小于6位!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (importWalletName.getText().toString().trim().length() <= 0) {
                    Toast.makeText(activity, "钱包名不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!passString.equals(repassString)) {
                    Toast.makeText(activity, "两次密码输入不一致!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!PassUtils.checkPass(passString)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    View daView = getLayoutInflater().inflate(R.layout.danger_pwd_dialog, null);
                    builder.setView(daView);
                    AlertDialog show = builder.show();
                    daView.findViewById(R.id.confirmBut).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String mne = importInPut.getText().toString().trim();
                            List<String> strings = Splitter.on(" ").splitToList(mne);
                            if (!checkMnemonic(strings)) {
                                Toast.makeText(activity, "导入失败请检查您的助记词!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            try {
                                ethCacheWallet = ConvertPojo.toETHCacheWallet(
                                        SecurityUtils.recoverWalletByMnemonic(
                                                importWalletName.getText().toString()
                                                , passString, mne, mnemoincTypeStr));
                                CacheWalletDao.writeCurrentJsonWallet(ethCacheWallet);
                                CacheWalletDao.writeJsonWallet(ethCacheWallet);
                                CoinPojo coinPojo = CoinDao.writeETHConinPojo();
                                startActivity(new Intent(activity, MainFragmentActivity.class));
                                activity.finish();
                            } catch (SecurityErrorException e) {
                                if (e.getErrorCode() == SecurityErrorException.ERROR_WALLET_PRIKEY_EXIST) {
                                    Toast.makeText(activity, "钱包已经存在", Toast.LENGTH_SHORT).show();
                                }
                                if (e.getErrorCode() == SecurityErrorException.ERROR_WALLET_AMOUNT_CROSS) {
                                    Toast.makeText(activity, "钱包数超出限制", Toast.LENGTH_SHORT).show();
                                }
                                e.printStackTrace();
                            }
                            show.dismiss();
                        }
                    });
                    daView.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            show.dismiss();
                        }
                    });
                } else {
                    String mne = importInPut.getText().toString().trim();
                    List<String> strings = Splitter.on(" ").splitToList(mne);
                    if (!checkMnemonic(strings)) {
                        Toast.makeText(activity, "导入失败请检查您的助记词!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        ethCacheWallet = ConvertPojo.toETHCacheWallet(
                                SecurityUtils.recoverWalletByMnemonic(
                                        importWalletName.getText().toString()
                                        , passString, mne, mnemoincTypeStr));
                        CacheWalletDao.writeCurrentJsonWallet(ethCacheWallet);
                        CacheWalletDao.writeJsonWallet(ethCacheWallet);
                        CoinPojo coinPojo = CoinDao.writeETHConinPojo();
                        startActivity(new Intent(activity, MainFragmentActivity.class));
                        activity.finish();
                    } catch (SecurityErrorException e) {
                        if (e.getErrorCode() == SecurityErrorException.ERROR_WALLET_PRIKEY_EXIST) {
                            Toast.makeText(activity, "钱包已经存在", Toast.LENGTH_SHORT).show();
                        }
                        if (e.getErrorCode() == SecurityErrorException.ERROR_WALLET_AMOUNT_CROSS) {
                            Toast.makeText(activity, "钱包数超出限制", Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    private boolean checkMnemonic(List<String> strings) {
        if (strings.size() != 12) return false;
        for (String s : strings) {
            if (s.length() < 1 || s.length() > 10) return false;
        }
        return true;
    }


    @Override
    public void update(String date) {
        Log.i("update", date);
    }
}
