package lr.com.wallet.dao;

import android.util.Log;

import com.google.common.base.Splitter;
import com.hunter.wallet.service.SecurityErrorException;
import com.hunter.wallet.service.WalletInfo;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.web3j.crypto.CipherException;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.ETHWalletResult;
import lr.com.wallet.utils.ConvertPojo;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.Md5Utils;
import lr.com.wallet.utils.SharedPreferencesUtils;

/**
 * Created by DT0814 on 2018/9/10.
 */

public class ETHWalletDao {
    private static String sfName = "ethWallet";
    private static String keyPre = "ethWallet";
    private static String ethWalletID = "ethWalletID";
    private static String ethWalletIDKey = "ethWalletIDKey";


    private static void writeStringWallet(ETHWallet ethWallet) {
        SharedPreferencesUtils.writeString(sfName, keyPre + "_" + ethWallet.getId(), JsonUtils.objectToJson(ethWallet));
    }

    private static int getId() {
        int id = SharedPreferencesUtils.getInt(ethWalletID, ethWalletIDKey);
        SharedPreferencesUtils.writeInt(ethWalletID, ethWalletIDKey, ++id);
        return id;
    }

    private static List<ETHWallet> getAllETHWallet() {
        List<ETHWallet> list = new ArrayList<>();
        Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
        all.forEach((k, v) -> {
            list.add(JsonUtils.jsonToPojo(v.toString(), ETHWallet.class));
        });
        return list;
    }

    private static ETHWallet getETHWalletById(int id) {
        for (ETHWallet ethWallet : getAllETHWallet()) {
            if (ethWallet.getId() == id) {
                return ethWallet;
            }
        }
        return null;
    }

    private static boolean exist(ETHWallet ethWallet) {
        for (ETHWallet wallet : getAllETHWallet()) {
            if (wallet.getPubKey().equals(ethWallet.getPubKey())) {
                return true;
            }
        }
        return false;
    }

    public static ETHWalletResult createWallet(String name, String password) {
        try {
            ETHWallet ethWallet = ETHWalletUtils.generateMnemonic(ETHWalletUtils.ETH_JAXX_TYPE, name, password);
            if (null == ethWallet) {
                return ETHWalletResult.err(0xFFFFFFFF);
            }
            if (exist(ethWallet)) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_PRIKEY_EXIST);
            }
            ethWallet.setId(getId());
            writeStringWallet(ethWallet);
            return ETHWalletResult.instance(ConvertPojo.toWalletInfo(ethWallet));
        } catch (Exception e) {
            e.printStackTrace();
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }

    public static ETHWalletResult getAllWalletInfo() {
        try {
            List<WalletInfo> list = new ArrayList<>();
            Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
            all.forEach((k, v) -> {
                list.add(ConvertPojo.toWalletInfo(JsonUtils.jsonToPojo(v.toString(), ETHWallet.class)));
            });
            if (null == list) {
                return ETHWalletResult.err(0xFFFFFFFF);
            }
            return ETHWalletResult.instance(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ETHWalletResult.err(0xFFFFFFFF);
        }

    }


    public static ETHWalletResult deleteWallet(int id, String password) {
        try {
            ETHWallet ethWallet = getETHWalletById(id);
            if (null == ethWallet) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_CANOT_FOUND);
            }
            if (!ethWallet.getPassword().equals(Md5Utils.md5(password))) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_PASSWORD_WRONG);
            }
            SharedPreferencesUtils.deleteString(sfName, keyPre + "_" + ethWallet.getId());
            return ETHWalletResult.instance(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }


    public static ETHWalletResult recoverByMnemonic(String name, String password, String mnemonic, String path) {
        try {
            try {
                MnemonicCode.INSTANCE.check(Splitter.on(" ").splitToList(mnemonic));
            } catch (MnemonicException e) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_MNEMONIC_INCORRECT);
            }

            ETHWallet ethWallet = ETHWalletUtils.importMnemonic(path, mnemonic, password, name);
            if (null == ethWallet) {
                return ETHWalletResult.err(0xFFFFFFFF);
            }
            if (exist(ethWallet)) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_PRIKEY_EXIST);
            }
            ethWallet.setId(getId());
            writeStringWallet(ethWallet);
            return ETHWalletResult.instance(ConvertPojo.toWalletInfo(ethWallet));
        } catch (Exception e) {
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }

    public static ETHWalletResult recoverByKeystore(String name, String password, String keystore) {
        try {
            Log.i("keystore", keystore);
            ETHWallet ethWallet = ETHWalletUtils.loadWalletByKeystore(keystore, password, name);
            if (null == ethWallet) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_KEYSTORE_RESOLVE_FAIL);
            }
            if (exist(ethWallet)) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_PRIKEY_EXIST);
            }
            ethWallet.setId(getId());
            writeStringWallet(ethWallet);
            return ETHWalletResult.instance(ConvertPojo.toWalletInfo(ethWallet));
        } catch (CipherException e) {
            return ETHWalletResult.err(SecurityErrorException.ERROR_KEYSTORE_RESOLVE_FAIL);
        } catch (Exception e) {
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }

    public static ETHWalletResult recoverByPrikey(String name, String password, byte[] prikey) {
        try {
            String pri = Numeric.toHexStringNoPrefix(prikey);
            Log.i("pri", pri);
            if (pri.length() != 64) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_PARAM_INCORRECT);
            }
            ETHWallet ethWallet = ETHWalletUtils.loadWalletByPrivateKey(pri, password, name);
            if (null == ethWallet) {
                return ETHWalletResult.err(0xFFFFFFFF);
            }
            if (exist(ethWallet)) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_PRIKEY_EXIST);
            }
            ethWallet.setId(getId());
            writeStringWallet(ethWallet);
            return ETHWalletResult.instance(ConvertPojo.toWalletInfo(ethWallet));
        } catch (Exception e) {
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }

    public static ETHWalletResult getKeystore(int id, String password) {
        try {
            ETHWallet ethWallet = getETHWalletById(id);
            if (null == ethWallet) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_CANOT_FOUND);
            }
            if (!ethWallet.getPassword().equals(Md5Utils.md5(password))) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_PASSWORD_WRONG);
            }
            String keyStore = ethWallet.getKeyStore();
            if (null == keyStore || keyStore.equals("")) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_GENERATE_KEYSTORE_FAIL);
            }
            return ETHWalletResult.instance(keyStore);
        } catch (Exception e) {
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }

    public static ETHWalletResult getMnemonic(int id, String password) {
        try {
            ETHWallet ethWallet = getETHWalletById(id);
            if (null == ethWallet) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_CANOT_FOUND);
            }
            if (!ethWallet.getPassword().equals(Md5Utils.md5(password))) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_PASSWORD_WRONG);
            }
            String mnemonic = ethWallet.getMnemonic();
            return ETHWalletResult.instance(mnemonic);
        } catch (Exception e) {
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }

    public static ETHWalletResult getPrikey(int id, String password) {

        try {
            ETHWallet ethWallet = getETHWalletById(id);
            if (null == ethWallet) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_CANOT_FOUND);
            }
            if (!ethWallet.getPassword().equals(Md5Utils.md5(password))) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_PASSWORD_WRONG);
            }
            String privateKey = ETHWalletUtils.derivePrivateKey(ethWallet, password);
            return ETHWalletResult.instance(Numeric.hexStringToByteArray(privateKey));
        } catch (Exception e) {
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }

    public static byte[] getPubkey(int id) {
        ETHWallet ethWallet = getETHWalletById(id);
        return ethWallet.getPubKey();
    }

    public static ETHWalletResult changeName(int id, String newName) {
        ETHWallet ethWallet = getETHWalletById(id);
        if (null == ethWallet) {
            return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_CANOT_FOUND);
        }
        ethWallet.setName(newName);
        writeStringWallet(ethWallet);
        return ETHWalletResult.instance(null);
    }

    public static ETHWalletResult changePassword(int id, String password, String newPassword) {
        ETHWallet ethWallet = getETHWalletById(id);
        if (null == ethWallet) {
            return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_CANOT_FOUND);
        }
        if (!ethWallet.getPassword().equals(Md5Utils.md5(password))) {
            return ETHWalletResult.err(SecurityErrorException.ERROR_PASSWORD_WRONG);
        }
        ethWallet.setPassword(Md5Utils.md5(newPassword));
        try {
            ethWallet.setKeyStore(ETHWalletUtils.changeKeyStore(ethWallet, password, newPassword));
        } catch (CipherException e) {
            return ETHWalletResult.err(0xFFFFFFFF);
        }
        writeStringWallet(ethWallet);
        return ETHWalletResult.instance(null);
    }
}
