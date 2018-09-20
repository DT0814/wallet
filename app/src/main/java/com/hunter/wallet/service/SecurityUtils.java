package com.hunter.wallet.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.*;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lr.com.wallet.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SecurityUtils {

    private static SecurityService securityService = null;
    private static Context appCtx;

    public static void init(Context context) {
        appCtx = context.getApplicationContext();
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
    private static final int BUF_LEN_PIN = 6;

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

    private static final String walletPlatformHost = "http://wallet.hdayun.com";
    private static final String sendAuthcodePath = walletPlatformHost + "/user/sendAuthcode";
    private static final String bindMobilePath = walletPlatformHost + "/user/bindMobile";
    private static final String rebindMobilePath = walletPlatformHost + "/user/rebindMobile";
    private static final String unlockWalletPath = walletPlatformHost + "/user/unlockWallet";
    private static final String resetWalletPath = walletPlatformHost + "/user/resetWallet";


    public static void sendAuthcode(String mobile, UserOperateCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("mobile", mobile)
                .build();
        Request request = new Request.Builder().url(sendAuthcodePath).post(formBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFail("network error!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                TypeReference<InvokeResult<Object>> typeReference = new TypeReference<InvokeResult<Object>>() {
                };
                InvokeResult<String> invokeResult = objectMapper.readValue(result, typeReference);
                if (invokeResult.isSuccess()) {
                    callback.onSuccess();
                } else {
                    callback.onFail(invokeResult.getMsg());
                }
            }
        });
    }

    public static UserInfo getUserInfo() throws SecurityErrorException {
        return securityService.getUserInfo();
    }

    private interface SignCallback {
        void onSuccess(String sign);

        void onFail(String msg);
    }

    private static void doUserOperateSign(String path, FormBody formBody, SignCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(path).post(formBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFail("network error!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                TypeReference<InvokeResult<String>> typeReference = new TypeReference<InvokeResult<String>>() {
                };
                InvokeResult<String> invokeResult = objectMapper.readValue(result, typeReference);
                if (invokeResult.isSuccess()) {
                    callback.onSuccess(invokeResult.getData());
                } else {
                    callback.onFail(invokeResult.getMsg());
                }
            }
        });
    }


    public interface UserOperateCallback {
        void onSuccess();

        void onFail(String msg);
    }

    /**
     *
     * @param pin PIN码
     * @param mobile 绑定手机
     * @param authcode 短信验证码
     * @param callback  回调
     * @throws SecurityErrorException
     */
    public static void userInit(String pin, String mobile, String authcode, UserOperateCallback callback) throws SecurityErrorException {

        if (pin.getBytes(UTF8).length != BUF_LEN_PIN) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_INCORRECT);
        }
        UserInfo userInfo = getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("mobile", mobile)
                .add("authcode", authcode)
                .add("deviceId", Numeric.toHexStringNoPrefix(userInfo.getDeviceId()))
                .build();
        doUserOperateSign(bindMobilePath, formBody, new SignCallback() {
            @Override
            public void onSuccess(String sign) {
                try {
                    securityService.userInit(pin.getBytes(UTF8), mobile, Numeric.hexStringToByteArray(sign));
                    callback.onSuccess();
                } catch (SecurityErrorException e) {
                    callback.onFail("init fail:" + e.getErrorCode());
                }
            }

            @Override
            public void onFail(String msg) {
                callback.onFail(msg);
            }
        });
    }

    /**
     *
     * @param pin 旧PIN
     * @param newPin 新PIN
     * @throws SecurityErrorException
     */
    public static void changePin(String pin, String newPin) throws SecurityErrorException {
        if (pin.getBytes(UTF8).length != BUF_LEN_PIN || newPin.getBytes(UTF8).length != BUF_LEN_PIN) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_INCORRECT);
        }
        securityService.changePin(pin.getBytes(UTF8), newPin.getBytes(UTF8));
    }


    /**
     *
     * @param pin
     * @param authcode 旧验证码
     * @param newMobile 新手机
     * @param newAuchcode 新验证码
     * @param callback 回调
     * @throws SecurityErrorException
     */
    public static void rebindMobile(String pin, String authcode, String newMobile, String newAuchcode, UserOperateCallback callback) throws SecurityErrorException {

        if (pin.getBytes(UTF8).length != BUF_LEN_PIN) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_INCORRECT);
        }
        UserInfo userInfo = getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("mobile", userInfo.getBindMobile())
                .add("authcode", authcode)
                .add("newMobile", newMobile)
                .add("newAuchcode", newAuchcode)
                .add("deviceId", Numeric.toHexStringNoPrefix(userInfo.getDeviceId()))
                .build();
        doUserOperateSign(rebindMobilePath, formBody, new SignCallback() {
            @Override
            public void onSuccess(String sign) {
                try {
                    securityService.rebindMobile(pin.getBytes(UTF8), newMobile, Numeric.hexStringToByteArray(sign));
                    callback.onSuccess();
                } catch (SecurityErrorException e) {
                    callback.onFail("init fail:" + e.getErrorCode());
                }
            }

            @Override
            public void onFail(String msg) {
                callback.onFail(msg);
            }
        });
    }

    /**
     *
     * @param authcode 验证码
     * @param callback 回调
     * @throws SecurityErrorException
     */
    public static void unlockWallet(String authcode, UserOperateCallback callback) throws SecurityErrorException {
        UserInfo userInfo = getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("mobile", userInfo.getBindMobile())
                .add("authcode", authcode)
                .add("userAuthcode", Numeric.toHexStringNoPrefix(userInfo.getAuthCode()))
                .add("deviceId", Numeric.toHexStringNoPrefix(userInfo.getDeviceId()))
                .build();
        doUserOperateSign(unlockWalletPath, formBody, new SignCallback() {
            @Override
            public void onSuccess(String sign) {
                try {
                    securityService.unlockWallet(Numeric.hexStringToByteArray(sign));
                    callback.onSuccess();
                } catch (SecurityErrorException e) {
                    callback.onFail("init fail:" + e.getErrorCode());
                }
            }

            @Override
            public void onFail(String msg) {
                callback.onFail(msg);
            }
        });
    }

    /**
     *
     * @param pin
     * @param authcode 验证码
     * @param callback 回调
     * @throws SecurityErrorException
     */
    public static void resetWallet(String pin, String authcode, UserOperateCallback callback) throws SecurityErrorException {
        if (pin.getBytes(UTF8).length != BUF_LEN_PIN) {
            throw new SecurityErrorException(SecurityErrorException.ERROR_PARAM_INCORRECT);
        }
        UserInfo userInfo = getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("mobile", userInfo.getBindMobile())
                .add("authcode", authcode)
                .add("userAuthcode", Numeric.toHexStringNoPrefix(userInfo.getAuthCode()))
                .add("deviceId", Numeric.toHexStringNoPrefix(userInfo.getDeviceId()))
                .build();
        doUserOperateSign(resetWalletPath, formBody, new SignCallback() {
            @Override
            public void onSuccess(String sign) {
                try {
                    securityService.resetWallet(pin.getBytes(UTF8), Numeric.hexStringToByteArray(sign));
                    callback.onSuccess();
                } catch (SecurityErrorException e) {
                    callback.onFail("init fail:" + e.getErrorCode());
                }
            }

            @Override
            public void onFail(String msg) {
                callback.onFail(msg);
            }
        });
    }
}
