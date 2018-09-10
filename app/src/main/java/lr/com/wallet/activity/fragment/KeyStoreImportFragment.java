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

import com.hunter.wallet.service.SecurityUtils;
import com.hunter.wallet.service.SecurityErrorException;

import lr.com.wallet.R;
import lr.com.wallet.activity.MainFragmentActivity;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.dao.CoinDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.utils.AppFilePath;
import lr.com.wallet.utils.ConvertPojo;

/**
 * Created by DT0814 on 2018/8/14.
 */

public class KeyStoreImportFragment extends Fragment {
    private FragmentActivity activity;
    private EditText importInPut;
    private EditText passWord;
    private EditText importWalletName;
    private ETHCacheWallet ethCacheWallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_ketstore_fragment, null);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        Context context = activity.getBaseContext();
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
                    ethCacheWallet = ConvertPojo.toETHCacheWallet(SecurityUtils.recoverWalletByKeystore(
                            importWalletName.getText().toString()
                            , passString
                            , importInPut.getText().toString()));
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
        });
        return view;
    }

}
