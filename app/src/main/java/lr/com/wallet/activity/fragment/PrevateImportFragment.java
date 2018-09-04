package lr.com.wallet.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityService;
import com.hunter.wallet.service.TeeErrorException;

import org.web3j.utils.Numeric;

import lr.com.wallet.R;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ConvertPojo;

/**
 * Created by DT0814 on 2018/8/14.
 */

public class PrevateImportFragment extends Fragment {
    private FragmentActivity activity;
    private EditText importInPut;
    private EditText passWord;
    private EditText reImportPassword;
    private EditText importWalletName;
    private ETHWallet ethWallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_private_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        Context context = activity.getBaseContext();
        AppFilePath.init(context);

        importInPut = view.findViewById(R.id.importInPut);
        passWord = view.findViewById(R.id.importPassword);
        reImportPassword = view.findViewById(R.id.reImportPassword);
        importWalletName = view.findViewById(R.id.importWalletName);

        Button button = view.findViewById(R.id.importButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passString = passWord.getText().toString();
                String repassString = reImportPassword.getText().toString();
                if (passString.trim().length() <= 5) {
                    Toast.makeText(activity, "密码长度不能小于6位!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (importWalletName.getText().toString().trim().length() <= 0) {
                    Toast.makeText(activity, "钱包名不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passString.equals(repassString)) {
                    Toast.makeText(activity, "两次密码输入不一致!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    ethWallet = ConvertPojo.toETHWallet(
                            SecurityService.recoverWalletByPrikey(
                                    importWalletName.getText().toString()
                                    , passString
                                    , Numeric.hexStringToByteArray(importInPut.getText().toString())));
                    WalletDao.writeCurrentJsonWallet(ethWallet);
                    WalletDao.writeJsonWallet(ethWallet);
                    CoinPojo coinPojo = CoinDao.writeETHConinPojo();
                    startActivity(new Intent(activity, MainFragmentActivity.class));
                    activity.finish();
                } catch (TeeErrorException e) {
                    if (e.getErrorCode() == TeeErrorException.TEE_ERROR_WALLET_PRIKEY_EXIST) {
                        Toast.makeText(activity, "钱包已经存在", Toast.LENGTH_SHORT).show();
                    }
                    if (e.getErrorCode() == TeeErrorException.TEE_ERROR_WALLET_AMOUNT_CROSS) {
                        Toast.makeText(activity, "钱包数超出限制", Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                    return;
                }

            }
        });
        return view;
    }
}
