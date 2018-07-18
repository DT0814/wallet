package lr.com.wallet.pojo;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TransactionBean {
    private String blockNumber;
    private String timeStamp;
    private String blockHash;
    private String transactionIndex;
    private String txreceipt_status;
    private String gasPrice;
    private String cumulativeGasUsed;
    private String nonce;
    private String hash;
    private String from;
    private String to;
    private String value;
    private String contractAddress;
    private String input;
    private String type;
    private String gas;
    private String gasUsed;
    private String traceId;
    private String isError;
    private String errCode;
    private String confirmations;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getIsError() {
        return isError;
    }

    public void setIsError(String isError) {
        this.isError = isError;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public TransactionBean() {
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getCumulativeGasUsed() {
        return cumulativeGasUsed;
    }

    public void setCumulativeGasUsed(String cumulativeGasUsed) {
        this.cumulativeGasUsed = cumulativeGasUsed;
    }

    public String getTxreceipt_status() {
        return txreceipt_status;
    }

    public void setTxreceipt_status(String txreceipt_status) {
        this.txreceipt_status = txreceipt_status;
    }


    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }

    @Override
    public String toString() {
        return "TransactionBean{" +
                "blockNumber='" + blockNumber + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", blockHash='" + blockHash + '\'' +
                ", transactionIndex='" + transactionIndex + '\'' +
                ", txreceipt_status='" + txreceipt_status + '\'' +
                ", gasPrice='" + gasPrice + '\'' +
                ", cumulativeGasUsed='" + cumulativeGasUsed + '\'' +
                ", nonce='" + nonce + '\'' +
                ", hash='" + hash + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", value='" + value + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", input='" + input + '\'' +
                ", type='" + type + '\'' +
                ", gas='" + gas + '\'' +
                ", gasUsed='" + gasUsed + '\'' +
                ", traceId='" + traceId + '\'' +
                ", isError='" + isError + '\'' +
                ", errCode='" + errCode + '\'' +
                ", confirmations='" + confirmations + '\'' +
                '}';
    }
}
