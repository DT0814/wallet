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

import org.web3j.crypto.CipherException;

import java.io.IOException;

import lr.com.wallet.R;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ConvertPojo;
import lr.com.wallet.utils.ETHWalletUtils;

/**
 * Created by DT0814 on 2018/8/14.
 */

public class KeyStoreImportFragment extends Fragment {
    private FragmentActivity activity;
    private View view;
    private Context context;
    private EditText importInPut;
    private EditText passWord;
    private EditText importWalletName;
    private ETHWallet ethWallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ketstore_import_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = activity.getBaseContext();
        AppFilePath.init(context);

        importInPut = view.findViewById(R.id.importInPut);
        passWord = view.findViewById(R.id.importPassword);
        importWalletName = view.findViewById(R.id.importWalletName);

        Button button = view.findViewById(R.id.importButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passString = passWord.getText().toString();
                if (passString.trim().length() <= 5) {
                    Toast.makeText(activity, "密码长度不能小于6位!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (importWalletName.getText().toString().trim().length() <= 0) {
                    Toast.makeText(activity, "钱包名不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    ethWallet = ConvertPojo.toETHWallet(SecurityService.recoverWalletByKeystore(
                            importWalletName.getText().toString()
                            , passString
                            , importInPut.getText().toString()));
                    WalletDao.writeCurrentJsonWallet(ethWallet);
                    WalletDao.writeJsonWallet(ethWallet);
                    CoinPojo coinPojo = CoinDao.writeETHConinPojo();
                    startActivity(new Intent(activity, MainFragmentActivity.class));
                } catch (TeeErrorException e) {
                    e.printStackTrace();
                    if (e.getErrorCode() == TeeErrorException.TEE_ERROR_WALLET_PRIKEY_EXIST) {
                        Toast.makeText(activity, "钱包已经存在", Toast.LENGTH_SHORT).show();
                    }
                } catch (CipherException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "密码错误!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

               /* ethWallet = ETHWalletUtils.loadWalletByKeystore(importInPut.getText().toString(),
                        passString,
                        importWalletName.getText().toString());
                if (null == ethWallet) {
                    Toast.makeText(activity, "密码错误!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (WalletDao.CheckContains(ethWallet)) {
                    Toast.makeText(activity, "当前钱包已存在!", Toast.LENGTH_LONG).show();
                    return;
                }
*/
            }
        });
        return view;
    }

}
