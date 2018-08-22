package com.hunter.wallet.service;

public class TeeErrorException extends Exception {


    /**
     * tee define
     */
    public static final int TEE_ERROR_CORRUPT_OBJECT = 0xF0100001;
    public static final int TEE_ERROR_CORRUPT_OBJECT_2 = 0xF0100002;
    public static final int TEE_ERROR_STORAGE_NOT_AVAILABLE = 0xF0100003;
    public static final int TEE_ERROR_STORAGE_NOT_AVAILABLE_2 = 0xF0100004;
    public static final int TEE_ERROR_GENERIC = 0xFFFF0000;
    public static final int TEE_ERROR_ACCESS_DENIED = 0xFFFF0001;
    public static final int TEE_ERROR_CANCEL = 0xFFFF0002;
    public static final int TEE_ERROR_ACCESS_CONFLICT = 0xFFFF0003;
    public static final int TEE_ERROR_EXCESS_DATA = 0xFFFF0004;
    public static final int TEE_ERROR_BAD_FORMAT = 0xFFFF0005;
    public static final int TEE_ERROR_BAD_PARAMETERS = 0xFFFF0006;
    public static final int TEE_ERROR_BAD_STATE = 0xFFFF0007;
    public static final int TEE_ERROR_ITEM_NOT_FOUND = 0xFFFF0008;
    public static final int TEE_ERROR_NOT_IMPLEMENTED = 0xFFFF0009;
    public static final int TEE_ERROR_NOT_SUPPORTED = 0xFFFF000A;
    public static final int TEE_ERROR_NO_DATA = 0xFFFF000B;
    public static final int TEE_ERROR_OUT_OF_MEMORY = 0xFFFF000C;
    public static final int TEE_ERROR_BUSY = 0xFFFF000D;
    public static final int TEE_ERROR_COMMUNICATION = 0xFFFF000E;
    public static final int TEE_ERROR_SECURITY = 0xFFFF000F;
    public static final int TEE_ERROR_SHORT_BUFFER = 0xFFFF0010;
    public static final int TEE_ERROR_EXTERNAL_CANCEL = 0xFFFF0011;
    public static final int TEE_ERROR_OVERFLOW = 0xFFFF300F;
    public static final int TEE_ERROR_TARGET_DEAD = 0xFFFF3024;
    public static final int TEE_ERROR_STORAGE_NO_SPACE = 0xFFFF3041;
    public static final int TEE_ERROR_MAC_INVALID = 0xFFFF3071;
    public static final int TEE_ERROR_SIGNATURE_INVALID = 0xFFFF3072;
    public static final int TEE_ERROR_TIME_NOT_SET = 0xFFFF5000;
    public static final int TEE_ERROR_TIME_NEEDS_RESET = 0xFFFF5001;
    public static final int TEE_ERROR_FILE_NOT_FOUND = 0xF57E0010;

    /**
     * customer define
     */
    public static final int TEE_ERROR_CMD_NOT_FOUND = 0xF57E1001;
    public static final int TEE_ERROR_PARAM_LEN_CROSS = 0xF57E1002;
    public static final int TEE_ERROR_BUF_LEN_SHORT = 0xF57E1003;
    public static final int TEE_ERROR_WALLET_NAME_EXIST = 0xF57E1004;
    public static final int TEE_ERROR_WALLET_PRIKEY_EXIST = 0xF57E1005;
    public static final int TEE_ERROR_WALLET_AMOUNT_CROSS = 0xF57E1006;
    public static final int TEE_ERROR_PASSWORD_WRONG = 0xF57E1007;
    public static final int TEE_ERROR_WALLET_CANOT_FOUND = 0xF57E1008;
    public static final int TEE_ERROR_PBKDF2_FAIL = 0xF57E1009;
    public static final int TEE_ERROR_RPMB_IO_FAIL = 0xF57E1010;
    public static final int TEE_ERROR_LOAD_WALLET_FAIL = 0xF57E1011;
    public static final int TEE_ERROR_SAVE_WALLET_FAIL = 0xF57E1012;
    public static final int TEE_ERROR_SIGN_FAIL = 0xF57E1013;
    public static final int TEE_ERROR_CREATE_PUBKEY_FAIL = 0xF57E1014;
    public static final int TEE_ERROR_MNEMONIC_AMOUNT_INCORRECT = 0xF57E1015;
    public static final int TEE_ERROR_MNEMONIC_WORD_CROSS = 0xF57E1016;
    public static final int TEE_ERROR_INTERFACE_UN_SUPPORT = 0xF57E1017;

    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public TeeErrorException(int errorCode) {
        super("Tee error : " + Integer.toHexString(errorCode));
        this.errorCode = errorCode;
    }
}
