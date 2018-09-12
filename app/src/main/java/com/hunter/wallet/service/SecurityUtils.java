package com.hunter.wallet.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.web3j.crypto.*;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lr.com.wallet.R;


public class SecurityUtils {

    private static SecurityService securityService = null;

    public static void init(Context context) {
        if (securityService == null) {
            try {
                System.loadLibrary("wsservice");
                securityService = new NativeSecurityService();
            } catch (Exception e) {
                securityService = new Web3jSecurityService(context);
            }
        }
    }

    private static final int AMOUNT_WALLET = 6;
    private static final int BUF_LEN_NAME = 32;
    private static final int BUF_LEN_PASSWORD = 32;
    private static final int BUF_LEN_MNEMONIC = 240;
    private static final int BUF_LEN_PRIKEY = 32;
    private static final int BUF_LEN_SIGNATURE = 68;
    private static final int BUF_LEN_KEYSTORE = 512;
    private static final int BUF_LEN_PUBKEY = 64;
    private static final int BUF_LEN_ADDR = 20;
    private static final int BUF_LEN_HASH256 = 32;
    private static final int BUF_LEN_HDKEY_PATH = 32;

    private static final Charset UTF8 = StandardCharsets.UTF_8;


    private SecurityUtils() {
    }

    public static List<WalletInfo> getWalletList() throws SecurityErrorException {
        return securityService.getAllWallet();
    }

    public static WalletInfo createWallet(String name, String password) throws SecurityErrorException {
        if (name.getBytes(UTF8).length >= BUF_LEN_NAME || password.getBytes(UTF8).length >= BUF_LEN_PASSWORD) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        return securityService.createWallet(name, password);
    }

    public static void deleteWallet(int id, String password) throws SecurityErrorException {
        if (password.getBytes(UTF8).length >= BUF_LEN_PASSWORD) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        securityService.deleteWallet(id, password);
    }


    public static WalletInfo recoverWalletByMnemonic(String name, String password, String mnemonic, String path) throws SecurityErrorException {
        if (name.getBytes(UTF8).length >= BUF_LEN_NAME
                || password.getBytes(UTF8).length >= BUF_LEN_PASSWORD
                || mnemonic.getBytes(UTF8).length >= BUF_LEN_MNEMONIC
                || path.getBytes(UTF8).length >= BUF_LEN_HDKEY_PATH) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        return securityService.recoverByMnemonic(name, password, mnemonic, path);
    }

    public static WalletInfo recoverWalletByKeystore(String name, String password, String keystore) throws SecurityErrorException {
        String keystoreNospace = keystore.toLowerCase().replaceAll("\\s*", "");
        if (name.getBytes(UTF8).length >= BUF_LEN_NAME
                || password.getBytes(UTF8).length >= BUF_LEN_PASSWORD
                || keystoreNospace.getBytes(UTF8).length >= BUF_LEN_KEYSTORE) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        return securityService.recoverByKeystore(name, password, keystoreNospace);
    }

    public static WalletInfo recoverWalletByPrikey(String name, String password, byte[] prikey) throws SecurityErrorException {
        if (name.getBytes(UTF8).length >= BUF_LEN_NAME
                || password.getBytes(UTF8).length >= BUF_LEN_PASSWORD
                || prikey.length > BUF_LEN_PRIKEY) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        return securityService.recoverByPrikey(name, password, prikey);
    }

    public static String getKeystore(int id, String password) throws SecurityErrorException {

        if (password.getBytes(UTF8).length >= BUF_LEN_PASSWORD) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        return securityService.getKeystore(id, password);
    }

    public static String getMnemonic(int id, String password) throws SecurityErrorException {
        if (password.getBytes(UTF8).length >= BUF_LEN_PASSWORD) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        return securityService.getMnemonic(id, password);
    }

    public static byte[] getPrikey(int id, String password) throws SecurityErrorException {
        if (password.getBytes(UTF8).length >= BUF_LEN_PASSWORD) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        return securityService.getPrikey(id, password);
    }


    public static byte[] getPubkey(int id) throws SecurityErrorException {
        return securityService.getPubkey(id);
    }

    public static String getAddr(int id) throws SecurityErrorException {
        return Numeric.toHexString(Keys.getAddress(getPubkey(id)));
    }


    public static void changeName(int id, String newName) throws SecurityErrorException {
        if (newName.getBytes(UTF8).length >= BUF_LEN_NAME) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        securityService.changeName(id, newName);
    }

    public static void changePassword(int id, String password, String newPassword) throws SecurityErrorException {
        if (password.getBytes(UTF8).length >= BUF_LEN_PASSWORD
                || newPassword.getBytes(UTF8).length >= BUF_LEN_PASSWORD) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        securityService.changePassword(id, password, newPassword);
    }

    public static byte[] signMessage(int id, String password, RawTransaction rawTransaction) throws SecurityErrorException {
        if (password.getBytes(UTF8).length >= BUF_LEN_PASSWORD) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_LEN_CROSS);
        }
        byte[] encodedTransaction = encode(rawTransaction);
        byte[] messageHash = Hash.sha3(encodedTransaction);

        byte[] signature = securityService.signature(id, password, messageHash);

        int rLen = signature[0];
        int sLen = signature[1 + rLen];
        byte[] r = Arrays.copyOfRange(signature, 1, 1 + rLen);
        byte[] s = Arrays.copyOfRange(signature, 2 + rLen, 2 + rLen + sLen);

        ECDSASignature sig = new ECDSASignature(new BigInteger(r), new BigInteger(s));
        int recId = -1;
        for (int i = 0; i < 4; i++) {
            BigInteger k = Sign.recoverFromSignature(i, sig, messageHash);
            if (k != null && k.equals(new BigInteger(1, getPubkey(id)))) {
                recId = i;
                break;
            }
        }
        if (recId == -1) {
            throw new RuntimeException("Could not construct a recoverable key. This should never happen.");
        }
        int headerByte = recId + 27;
        return encode(rawTransaction, new Sign.SignatureData((byte) headerByte, r, s));

    }


    //<uses-permission android:name="android.permission.FORCE_STOP_PACKAGES"/>

    /**
     * 关闭非系统应用
     *
     * @param context
     */
    private static void shutdownOtherApp(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        try {
            Method method = Class.forName("android.app.ActivityManager")
                    .getMethod("forceStopPackage", String.class);
            for (PackageInfo packageInfo : packages) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //mActivityManager.forceStopPackage(packageInfo.packageName);
                    method.invoke(mActivityManager, packageInfo.packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CheckEnvCallback {
        void onSuccess();

        void onFail();
    }

    public static void checkEnv(Activity activity, CheckEnvCallback callback) {
        if (securityService instanceof NativeSecurityService) {
            AlertDialog.Builder reminderBuilder = new AlertDialog.Builder(activity);
            View reminderView = activity.getLayoutInflater().inflate(R.layout.reminder_layout, null);
            reminderBuilder.setView(reminderView);
            AlertDialog show = reminderBuilder.show();
            show.setCancelable(false);
            reminderView.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show.dismiss();
                    callback.onFail();
                }
            });
            reminderView.findViewById(R.id.agreeBut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SecurityUtils.shutdownOtherApp(activity);
                    callback.onSuccess();
                    show.dismiss();
                }
            });
        } else {
            callback.onSuccess();
        }
    }


    private static byte[] encode(RawTransaction rawTransaction) {
        return encode(rawTransaction, null);
    }

    private static byte[] encode(RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> values = asRlpValues(rawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    private static List<RlpType> asRlpValues(RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> result = new ArrayList<>();

        result.add(RlpString.create(rawTransaction.getNonce()));
        result.add(RlpString.create(rawTransaction.getGasPrice()));
        result.add(RlpString.create(rawTransaction.getGasLimit()));

        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(rawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));

        if (signatureData != null) {
            result.add(RlpString.create(signatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
        }

        return result;
    }
}
