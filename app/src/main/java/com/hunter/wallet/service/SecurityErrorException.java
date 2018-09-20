package com.hunter.wallet.service;

public class SecurityErrorException extends Exception {

    /**
     * customer define
     */
    public static final int ERROR_CMD_NOT_FOUND = 0xF57E1001;
    public static final int ERROR_WALLET_PRIKEY_EXIST = 0xF57E1002;
    public static final int ERROR_WALLET_AMOUNT_CROSS = 0xF57E1003;
    public static final int ERROR_WALLET_CANOT_FOUND = 0xF57E1004;
    public static final int ERROR_PASSWORD_WRONG = 0xF57E1005;
    public static final int ERROR_CREATE_FILE_FAIL = 0xF57E1006;
    public static final int ERROR_WRITE_FILE_FAIL = 0xF57E1007;
    public static final int ERROR_READ_FILE_FAIL = 0xF57E1008;
    public static final int ERROR_WALLET_DATA_INCORRECT = 0xF57E1009;
    public static final int ERROR_SIGN_FAIL = 0xF57E1010;
    public static final int ERROR_MNEMONIC_INCORRECT = 0xF57E1011;
    public static final int ERROR_PARAM_INCORRECT = 0xF57E1012;
    public static final int ERROR_KEYSTORE_RESOLVE_FAIL = 0xF57E1013;
    public static final int ERROR_GENERATE_KEYSTORE_FAIL = 0xF57E1014;
    public static final int ERROR_OUTBUF_INCORRECT = 0xF57E1015;
    public static final int ERROR_MNEMONIC_TRANSFER_FAIL = 0xF57E1016;
    public static final int ERROR_GENERATE_PUBKEY_FAIL = 0xF57E1017;

    public static final int ERROR_PARAM_LEN_CROSS = 0xF57E1018;

    public static final int ERROR_NEEWORK_FAIL = 0xF57E1019;

    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public SecurityErrorException(int errorCode) {
        super("error : " + Integer.toHexString(errorCode));
        this.errorCode = errorCode;
    }
}
