package lr.com.wallet.dao;

import com.hunter.wallet.service.SecurityErrorException;
import com.hunter.wallet.service.WalletInfo;

import org.web3j.utils.Numeric;

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


    public static void writeStringWallet(ETHWallet ethWallet) {
        SharedPreferencesUtils.writeString(sfName, keyPre + "_" + ethWallet.getId(), JsonUtils.objectToJson(ethWallet));
    }

    public static int getId() {
        int id = SharedPreferencesUtils.getInt(ethWalletID, ethWalletIDKey);
        SharedPreferencesUtils.writeInt(ethWalletID, ethWalletIDKey, ++id);
        return id;
    }

    public static List<ETHWallet> getAllETHWallet() {
        List<ETHWallet> list = new ArrayList<>();
        Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
        all.forEach((k, v) -> {
            list.add(JsonUtils.jsonToPojo(v.toString(), ETHWallet.class));
        });
        return list;
    }

    public static ETHWallet getETHWalletById(int id) {
        for (ETHWallet ethWallet : getAllETHWallet()) {
            if (ethWallet.getId() == id) {
                return ethWallet;
            }
        }
        return null;
    }


    public static ETHWalletResult createWallet(String name, String password) {
        ETHWallet ethWallet = ETHWalletUtils.generateMnemonic(ETHWalletUtils.ETH_JAXX_TYPE, name, password);
        if (null == ethWallet) {
            return ETHWalletResult.err(0xFFFFFFFF);
        }
        for (ETHWallet wallet : getAllETHWallet()) {
            if (wallet.getPubKey().equals(ethWallet.getPubKey())) {
                return ETHWalletResult.err(SecurityErrorException.ERROR_WALLET_PRIKEY_EXIST);
            }
        }
        ethWallet.setId(getId());
        writeStringWallet(ethWallet);
        return ETHWalletResult.instance(ConvertPojo.toWalletInfo(ethWallet));
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
            if (ethWallet.getPassword().equals(Md5Utils.md5(password))) {
                SharedPreferencesUtils.deleteString(sfName, keyPre + "_" + ethWallet.getId());
                return ETHWalletResult.instance(null);
            }
            return ETHWalletResult.err(SecurityErrorException.ERROR_PASSWORD_WRONG);
        } catch (Exception e) {
            e.printStackTrace();
            return ETHWalletResult.err(0xFFFFFFFF);
        }
    }


    public static WalletInfo recoverByMnemonic(String name, String password, String mnemonic, String path) {
        ETHWallet ethWallet = ETHWalletUtils.importMnemonic(path, mnemonic, password, name);
        if (null == ethWallet) {
            return null;
        }
        ethWallet.setId(getId());
        writeStringWallet(ethWallet);
        return ConvertPojo.toWalletInfo(ethWallet);
    }

    public static WalletInfo recoverByKeystore(String name, String password, String keystore) {
        ETHWallet ethWallet = ETHWalletUtils.loadWalletByKeystore(keystore, password, name);
        if (null == ethWallet) {
            return null;
        }
        ethWallet.setId(getId());
        writeStringWallet(ethWallet);
        return ConvertPojo.toWalletInfo(ethWallet);
    }

    public static WalletInfo recoverByPrikey(String name, String password, byte[] prikey) {
        ETHWallet ethWallet = ETHWalletUtils.loadWalletByPrivateKey(Numeric.toHexString(prikey), password, name);
        if (null == ethWallet) {
            return null;
        }
        ethWallet.setId(getId());
        writeStringWallet(ethWallet);
        return ConvertPojo.toWalletInfo(ethWallet);
    }

    public static String getKeystore(int id, String password) {
        ETHWallet ethWallet = getETHWalletById(id);
        if (ethWallet.getPassword().equals(Md5Utils.md5(password))) {
            return ethWallet.getKeyStore();
        }
        return null;
    }

    public static String getMnemonic(int id, String password) {
        ETHWallet ethWallet = getETHWalletById(id);
        if (ethWallet.getPassword().equals(Md5Utils.md5(password))) {
            return ethWallet.getMnemonic();
        }
        return null;
    }

    public static byte[] getPrikey(int id, String password) {
        ETHWallet ethWallet = getETHWalletById(id);
        if (ethWallet.getPassword().equals(Md5Utils.md5(password))) {
            String privateKey = ETHWalletUtils.derivePrivateKey(ethWallet, password);
            return Numeric.hexStringToByteArray(privateKey);
        }
        return null;
    }

    public static byte[] getPubkey(int id) {
        ETHWallet ethWallet = getETHWalletById(id);
        return Numeric.hexStringToByteArray(ethWallet.getPubKey());
    }
}
