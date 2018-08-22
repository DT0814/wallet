package com.hunter.wallet.service;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SecurityService {

    public static final int PRIVATE_KEY_SIZE = 32;
    public static final int PUBLIC_KEY_SIZE = 64;

    private static final int TEE_RESULT_LEN = 4;

    private static final int TEE_SUCCESS = 0x00000000;

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private static final NativeSecurityService nativeService = NativeSecurityService.getInstance();

    private static List<EthWallet> cacheList = null;

    private SecurityService() {
    }

    private static void reloadWalletList() throws TeeErrorException {
        cacheList = null;
        byte[] ret = nativeService.getWalletList();
        int code = byte4ToInt(ret, 0, true);
        if (code == TEE_SUCCESS) {
            cacheList = walletListAnalysis(ret, TEE_RESULT_LEN);
        } else {
            Log.e("SecurityService", "reloadWalletList fail: " + Integer.toHexString(code));
            throw new TeeErrorException(code);
        }
        updateBalanceAndNum();
    }

    private static List<EthWallet> getCacheList() throws TeeErrorException {
        if (cacheList == null) {
            reloadWalletList();
        }
        return cacheList;
    }

    /**
     * 钱包列表解析
     *
     * @param buf
     * @param offset
     * @return
     */
    private static List<EthWallet> walletListAnalysis(byte[] buf, int offset) {
        List<EthWallet> list = new ArrayList<>();
        int p = offset;
        int total = buf[p];
        p++;
        for (int i = total; i > 0; i--) {
            int id = byte4ToInt(buf, p, true);
            p += 4;
            byte[] pubkey = new byte[PUBLIC_KEY_SIZE];
            System.arraycopy(buf, p, pubkey, 0, PUBLIC_KEY_SIZE);
            p += PUBLIC_KEY_SIZE;
            int strlen = buf[p];
            p++;
            byte[] name = new byte[strlen];
            System.arraycopy(buf, p, name, 0, strlen);
            p += strlen;

            EthWallet ethWallet = new EthWallet();
            ethWallet.setId(id);
            ethWallet.setName(new String(name, UTF8));
            ethWallet.setPubkey(pubkey);
            list.add(ethWallet);
        }
        return list;
    }

    /**
     * 获取钱包列表
     *
     * @return
     */
    public static List<EthWallet> getWalletList() throws TeeErrorException {
        List<EthWallet> wallets = new ArrayList<>();
        for (EthWallet ethWallet : getCacheList()) {
            wallets.add(new EthWallet(ethWallet));
        }
        return wallets;
    }


    /**
     * 获取助记词
     *
     * @param id
     * @param password
     * @return
     */
    public static List<String> getMnemonic(int id, String password) throws TeeErrorException {

        byte[] ret = nativeService.getMnemonic(id, password.getBytes(UTF8));
        int code = byte4ToInt(ret, 0, true);
        if (code == TEE_SUCCESS) {
            return bufToStrList(ret, TEE_RESULT_LEN);
        } else {
            throw new TeeErrorException(code);
        }
    }


    /**
     * 获取私钥
     *
     * @param id
     * @param password
     * @return
     */
    public static byte[] getPrikey(int id, String password) throws TeeErrorException {

        byte[] ret = nativeService.getPrikey(id, password.getBytes(UTF8));
        int code = byte4ToInt(ret, 0, true);
        if (code == TEE_SUCCESS) {
            byte[] prikey = new byte[PRIVATE_KEY_SIZE];
            System.arraycopy(ret, TEE_RESULT_LEN, prikey, 0, PRIVATE_KEY_SIZE);
            return prikey;
        } else {
            throw new TeeErrorException(code);
        }
    }

    /**
     * 获取keystore
     *
     * @param id
     * @param password
     * @return
     */
    public static String getKeystore(int id, String password) throws TeeErrorException, CipherException, IOException {
        byte[] prikey = getPrikey(id, password);
        StringWriter writer = new StringWriter();
        new ObjectMapper().writeValue(writer, Wallet.createLight(password, ECKeyPair.create(prikey)));
        return writer.toString();
    }

    /**
     * 获取公钥
     *
     * @param id
     * @return
     */
    public static byte[] getPubkey(int id) throws TeeErrorException {
        return getWallet(id).getPubkey();
    }


    /**
     * 获取地址
     *
     * @param id
     * @return
     */
    public static String getAddr(int id) throws TeeErrorException {
        return getWallet(id).getAddr();
    }


    private static EthWallet ethWalletAnalysis(byte[] buf, int offset) {
        int p = offset;
        int id = byte4ToInt(buf, p, true);
        p += 4;
        byte[] pubkey = new byte[PUBLIC_KEY_SIZE];
        System.arraycopy(buf, p, pubkey, 0, PUBLIC_KEY_SIZE);
        p += PUBLIC_KEY_SIZE;
        int strLen = buf[p];
        p++;
        byte[] nameBytes = new byte[strLen];
        System.arraycopy(buf, p, nameBytes, 0, strLen);
        EthWallet ethWallet = new EthWallet();
        ethWallet.setId(id);
        ethWallet.setName(new String(nameBytes, UTF8));
        ethWallet.setPubkey(pubkey);
        return ethWallet;
    }

    /**
     * 创建钱包
     *
     * @param name
     * @param password
     * @return
     */
    public static EthWallet createWallet(String name, String password) throws TeeErrorException {
        byte[] ret = nativeService.createWallet(name.getBytes(UTF8), password.getBytes(UTF8));
        int code = byte4ToInt(ret, 0, true);
        if (code == TEE_SUCCESS) {
            reloadWalletList();
            return ethWalletAnalysis(ret, TEE_RESULT_LEN);
        } else {
            throw new TeeErrorException(code);
        }
    }


    /**
     * 删除钱包
     *
     * @param id
     * @param password
     * @throws TeeErrorException
     */
    public static void deleteWallet(int id, String password) throws TeeErrorException {
        byte[] ret = nativeService.deleteWallet(id, password.getBytes(UTF8));
        int code = byte4ToInt(ret, 0, true);
        if (code != TEE_SUCCESS) {
            throw new TeeErrorException(code);
        }
        reloadWalletList();
    }

    /**
     * 恢复钱包
     *
     * @param name
     * @param password
     * @param mnemonic
     * @return
     */
    public static EthWallet recoverWalletByMnemonic(String name, String password, List<String> mnemonic) throws TeeErrorException {
        byte[] ret = nativeService.recoverWalletByMnemonic(name.getBytes(UTF8), password.getBytes(UTF8), strListToBuf(mnemonic));
        int code = byte4ToInt(ret, 0, true);
        if (code == TEE_SUCCESS) {
            reloadWalletList();
            return ethWalletAnalysis(ret, TEE_RESULT_LEN);
        } else {
            throw new TeeErrorException(code);
        }
    }


    /**
     * 恢复钱包
     *
     * @param name
     * @param password
     * @param keystore
     * @return
     */
    public static EthWallet recoverWalletByKeystore(String name, String password, String keystore) throws TeeErrorException, CipherException, IOException {

        WalletFile walletFile = new ObjectMapper().readValue(keystore, WalletFile.class);
        ECKeyPair ecKeyPair = Wallet.decrypt(password, walletFile);

        byte[] ret = nativeService.recoverWalletByPrikey(name.getBytes(UTF8), password.getBytes(UTF8), Numeric.toBytesPadded(ecKeyPair.getPrivateKey(), 32));
        int code = byte4ToInt(ret, 0, true);
        if (code == TEE_SUCCESS) {
            reloadWalletList();
            return ethWalletAnalysis(ret, TEE_RESULT_LEN);
        } else {
            throw new TeeErrorException(code);
        }
    }

    /**
     * 恢复钱包
     *
     * @param name
     * @param password
     * @param prikey
     * @return
     */
    public static EthWallet recoverWalletByPrikey(String name, String password, byte[] prikey) throws TeeErrorException {
        byte[] ret = nativeService.recoverWalletByPrikey(name.getBytes(UTF8), password.getBytes(UTF8), prikey);
        int code = byte4ToInt(ret, 0, true);
        if (code == TEE_SUCCESS) {
            reloadWalletList();
            return ethWalletAnalysis(ret, TEE_RESULT_LEN);
        } else {
            throw new TeeErrorException(code);
        }
    }

    public static EthWallet getWallet(int id) throws TeeErrorException {
        for (EthWallet ethWallet : getCacheList()) {
            if (ethWallet.getId() == id) {
                return ethWallet;
            }
        }
        return null;
    }

    /**
     * 签名
     *
     * @param id
     * @param rawTransaction
     * @return
     */
    public static byte[] signMessage(int id, String password, RawTransaction rawTransaction) throws TeeErrorException {
        byte[] encodedTransaction = encode(rawTransaction);
        byte[] messageHash = Hash.sha3(encodedTransaction);
        Log.i("wallet messageHash", Numeric.toHexString(messageHash));

        byte[] ret = nativeService.signature(id, password.getBytes(UTF8), messageHash);
        int code = byte4ToInt(ret, 0, true);
        if (code == TEE_SUCCESS) {

            int rLen = ret[4];
            int sLen = ret[5 + rLen];
            byte[] r = new byte[rLen];
            byte[] s = new byte[sLen];
            System.arraycopy(ret, 5, r, 0, rLen);
            System.arraycopy(ret, 6 + rLen, s, 0, sLen);
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
        } else {
            throw new TeeErrorException(code);
        }


    }

    /**
     * 修改钱包名
     *
     * @param id
     * @param newName
     * @throws TeeErrorException
     */
    public static void changeName(int id, String newName) throws TeeErrorException {
        byte[] ret = nativeService.changeName(id, newName.getBytes(UTF8));
        int code = byte4ToInt(ret, 0, true);
        if (code != TEE_SUCCESS) {
            throw new TeeErrorException(code);
        }
        reloadWalletList();
    }

    /**
     * 修改钱包密码
     *
     * @param id
     * @param oldPassword
     * @param newPassword
     * @throws TeeErrorException
     */
    public static void changePassword(int id, String oldPassword, String newPassword) throws TeeErrorException {
        byte[] ret = nativeService.changePassword(id, oldPassword.getBytes(UTF8), newPassword.getBytes(UTF8));
        int code = byte4ToInt(ret, 0, true);
        if (code != TEE_SUCCESS) {
            throw new TeeErrorException(code);
        }
        reloadWalletList();
    }

    public static void updateBalanceAndNum() {
        if (cacheList != null) {
            for (EthWallet ethWallet : cacheList) {
                //TODO
            }
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

    private static byte[] strListToBuf(List<String> strList) {

        if (strList != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(strList.size());
            for (String str : strList) {
                try {
                    byte[] strBytes = str.getBytes(UTF8);
                    baos.write(strBytes.length);
                    baos.write(strBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return baos.toByteArray();
        }
        return null;
    }

    private static List<String> bufToStrList(byte[] buf, int offset) {
        List<String> list = new ArrayList<>();
        if (buf != null && buf.length > offset) {
            int total = buf[offset];
            for (int i = offset + 1; total > 0 && i < buf.length - offset; total--) {
                int strLen = buf[i];
                i++;
                byte[] str = new byte[strLen];
                System.arraycopy(buf, i, str, 0, strLen);
                list.add(new String(str, UTF8));
                i += strLen;
            }
            if (list.size() != buf[offset]) {
                throw new RuntimeException("error str list buf！");
            }
        }
        return list;
    }

    public static int byte4ToInt(byte[] bytes, int off, boolean bigEndian) {
        if (bigEndian) {
            int b0 = bytes[off] & 0xFF;
            int b1 = bytes[off + 1] & 0xFF;
            int b2 = bytes[off + 2] & 0xFF;
            int b3 = bytes[off + 3] & 0xFF;
            return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        } else {
            int b3 = bytes[off] & 0xFF;
            int b2 = bytes[off + 1] & 0xFF;
            int b1 = bytes[off + 2] & 0xFF;
            int b0 = bytes[off + 3] & 0xFF;
            return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        }
    }

}
