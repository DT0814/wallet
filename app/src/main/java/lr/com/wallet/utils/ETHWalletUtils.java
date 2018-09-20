package lr.com.wallet.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.pojo.ETHWallet;

/**
 * Created by dt0814 on 2018/7/13.
 */

public class ETHWalletUtils {
    private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();
    public static String ETH_JAXX_TYPE = "m/44'/60'/0'/0/0";
    public static String ETH_LEDGER_TYPE = "m/44'/60'/0'/0";
    public static String ETH_CUSTOM_TYPE = "m/44'/60'/1'/0/0";

    /**
     * 创建助记词，并通过助记词创建钱包
     *
     * @param walletName
     * @param pwd
     * @return
     */
    public static ETHWallet generateMnemonic(String path, String walletName, String pwd) throws CipherException {
        String[] pathArray = path.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;

        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }

    public static String changeKeyStore(ETHWallet ethWallet, String pwd, String newPassword) throws CipherException {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(derivePrivateKey(ethWallet, pwd)));
        WalletFile walletFile = Wallet.create(newPassword, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        return JsonUtils.objectToJson(walletFile);
    }

    /**
     * @param walletName 钱包名称
     * @param ds         助记词加密种子
     * @param pathArray  助记词标准
     * @param pwd        密码
     * @return
     */
    @Nullable
    private static ETHWallet generateWalletByMnemonic(String walletName, DeterministicSeed ds,
                                                      String[] pathArray, String pwd) throws CipherException {
        //种子
        byte[] seedBytes = ds.getSeedBytes();
        //助记词
        List<String> mnemonic = ds.getMnemonicCode();
        if (seedBytes == null)
            return null;
        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }
        ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
        ETHWallet ethWallet = generateWallet(walletName, pwd, keyPair);
        if (ethWallet != null) {
            ethWallet.setMnemonic(convertMnemonicList(mnemonic));
        }
        return ethWallet;
    }

    private static String convertMnemonicList(List<String> mnemonics) {
        StringBuilder sb = new StringBuilder();
        for (String mnemonic : mnemonics
                ) {
            sb.append(mnemonic);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    @Nullable
    private static ETHWallet generateWallet(String walletName, String pwd, ECKeyPair ecKeyPair) throws CipherException {
        WalletFile walletFile;
        walletFile = Wallet.create(pwd, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        ETHWallet ethWallet = new ETHWallet();
        ethWallet.setName(walletName);
        ethWallet.setAddress(Keys.toChecksumAddress(walletFile.getAddress()));
        ethWallet.setKeyStore(JsonUtils.objectToJson(walletFile));
        ethWallet.setPassword(Md5Utils.md5(pwd));
        byte[] bytes = ecKeyPair.getPublicKey().toByteArray();
        ethWallet.setPubKey(bytes);
        return ethWallet;
    }

    /**
     * 通过keystore.json文件导入钱包
     *
     * @param keystore 原json文件
     * @param pwd      json文件密码
     * @return
     */
    public static ETHWallet loadWalletByKeystore(String keystore, String pwd, String walletName) throws IOException, CipherException {
        Credentials credentials = null;
        WalletFile walletFile = null;
        walletFile = objectMapper.readValue(keystore, WalletFile.class);
        credentials = Credentials.create(Wallet.decrypt(pwd, walletFile));

        if (credentials != null) {
            return generateWallet(walletName, pwd, credentials.getEcKeyPair());
        }
        return null;
    }

    /**
     * 通过导入助记词，导入钱包
     *
     * @param path     路径
     * @param mnemonic 助记词
     * @param pwd      密码
     * @return
     */
    public static ETHWallet importMnemonic(String path, String mnemonic, String pwd, String walletname) throws CipherException {
        String[] pathArray = path.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(Arrays.asList(mnemonic.split(" ")), null, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletname, ds, pathArray, pwd);
    }

    /**
     * 通过明文私钥导入钱包
     *
     * @param privateKey
     * @param pwd
     * @return
     */
    public static ETHWallet loadWalletByPrivateKey(String privateKey, String pwd, String walletName) throws CipherException {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return generateWallet(walletName, pwd, ecKeyPair);
    }

    /**
     * 导出地址
     *
     * @return
     * @ethWallet 钱包
     */

    /**
     * 导出明文私钥
     *
     * @param ethWallet 需要导出私钥的钱包
     * @param pwd       钱包密码
     * @return
     */
    public static String derivePrivateKey(ETHWallet ethWallet, String pwd) {
        Credentials credentials;
        ECKeyPair keypair;
        String privateKey = null;
        try {
            WalletFile walletFile = objectMapper.readValue(ethWallet.getKeyStore(), WalletFile.class);
            credentials = Credentials.create(Wallet.decrypt(pwd, walletFile));
            keypair = credentials.getEcKeyPair();
            privateKey = Numeric.toHexStringNoPrefixZeroPadded(keypair.getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 导出keystore文件
     *
     * @return
     * @ethWallet 导出keyStore的钱包
     */
    public static String deriveKeystore(ETHWallet Wallet) {
        String keystore = null;
        WalletFile walletFile;
        try {
            walletFile = objectMapper.readValue(Wallet.getKeyStore(), WalletFile.class);
            keystore = objectMapper.writeValueAsString(walletFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keystore;
    }

    public static void switchTouXiangImg(ImageView touXiangImg, int tongxingID) {
        switch (tongxingID % 12) {
            case 11:
                touXiangImg.setImageResource(R.drawable.touxiang_12);
                break;
            case 10:
                touXiangImg.setImageResource(R.drawable.touxiang_11);
                break;
            case 9:
                touXiangImg.setImageResource(R.drawable.touxiang_10);
                break;
            case 8:
                touXiangImg.setImageResource(R.drawable.touxiang_9);
                break;
            case 7:
                touXiangImg.setImageResource(R.drawable.touxiang_8);
                break;
            case 6:
                touXiangImg.setImageResource(R.drawable.touxiang_7);
                break;
            case 5:
                touXiangImg.setImageResource(R.drawable.touxiang_6);
                break;
            case 4:
                touXiangImg.setImageResource(R.drawable.touxiang_5);
                break;
            case 3:
                touXiangImg.setImageResource(R.drawable.touxiang_4);
                break;
            case 2:
                touXiangImg.setImageResource(R.drawable.touxiang_3);
                break;
            case 1:
                touXiangImg.setImageResource(R.drawable.touxiang_2);
                break;
            case 0:
                touXiangImg.setImageResource(R.drawable.touxiang_1);
                break;
            default:
                touXiangImg.setImageResource(R.drawable.touxiang_1);
                break;
        }
    }
}
