package lr.com.wallet.utils;

import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by dt0814 on 2018/7/21.
 */

public class CoinUtils {
    //public static final String URL = "https://mainnet.infura.io/c0oGHqQQlq6XJU2kz5DL";
    private static Web3j web3j;
    public static final String URL = "https://rinkeby.infura.io/c0oGHqQQlq6XJU2kz5DL";

    public static Web3j getWeb3j() {
        if (null == web3j) {
            return Web3j.build(new HttpService(URL));
        } else {
            return web3j;
        }
    }

    public static BigInteger getGasPrice() throws IOException {
        EthGasPrice ethGasPrice = Web3jUtil.getWeb3j().ethGasPrice().send();
        BigInteger gasPrice = null;
        if (ethGasPrice.hasError()) {
        } else {
            gasPrice = ethGasPrice.getGasPrice();
        }
        return gasPrice;
    }

    public static BigInteger getEstimateGas(String from, String to) throws IOException {
        Transaction transaction = Transaction.createEthCallTransaction(from, to, null);
        EthEstimateGas ethEstimateGas = Web3jUtil.getWeb3j().ethEstimateGas(transaction).send();
        BigInteger estimateGas = null;
        if (ethEstimateGas.hasError()) {
            System.out.println(ethEstimateGas.getError().getMessage());
        } else {
            estimateGas = ethEstimateGas.getAmountUsed();
        }
        return estimateGas;
    }

    public static String getSymbolName(String contractAddress) {
        Web3j web3j = getWeb3j();
        String symbol = null;
        String fromAddr = "0x59dd7dfb072c1c80ceec4d08588a01603c5d3bf0";
        String methodName = "symbol";
        try {
            List<Type> inputParameters = new ArrayList<>();
            List<TypeReference<?>> outputParameters = new ArrayList();
            TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
            };
            outputParameters.add(typeReference);

            Function function = new Function(methodName, inputParameters, outputParameters);

            String data = FunctionEncoder.encode(function);
            Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

            EthCall ethCall;

            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            if (ethCall.hasError()) {
                System.out.println(ethCall.getError().getMessage());
            }
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            symbol = results.get(0).getValue().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return symbol;
    }

    public static String getName(String contractAddress) {
        Web3j web3j = getWeb3j();
        String symbol = null;
        String fromAddr = "0x59dd7dfb072c1c80ceec4d08588a01603c5d3bf0";
        String methodName = "name";
        try {
            List<Type> inputParameters = new ArrayList<>();
            List<TypeReference<?>> outputParameters = new ArrayList();
            TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
            };
            outputParameters.add(typeReference);

            Function function = new Function(methodName, inputParameters, outputParameters);

            String data = FunctionEncoder.encode(function);
            Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

            EthCall ethCall;

            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            if (ethCall.hasError()) {
                System.out.println(ethCall.getError().getMessage());
            }
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            symbol = results.get(0).getValue().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return symbol;
    }

    public static String getBalanceOf(String contractAddress, String address) {
        Web3j web3j = getWeb3j();
        String value = null;
        try {
            value = web3j.ethCall(
                    Transaction.createEthCallTransaction(address,
                            contractAddress,
                            "0x70a08231000000000000000000000000" + address.substring(2, address.length())),
                    DefaultBlockParameterName.PENDING).send().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i;
        for (i = 2; i < value.length(); i++) {
            if (value.charAt(i) != '0') {
                break;
            }
        }
        value = value.substring(i, value.length());
        if (value.equals("")) {
            return "0";
        }
        long l = Long.parseLong(value, 16);
        return l + "";
    }

    public static String transaction(String from, String to, String contractAddress, String prvKey, String coinCount) {
        try {
            Web3j web3j = getWeb3j();
            EthGetTransactionCount ethGetTransactionCount = null;
            ethGetTransactionCount = web3j.ethGetTransactionCount(
                    from, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            Uint256 uint256 = new Uint256(new BigInteger(coinCount));
            Function function = new Function(
                    "transfer",//交易的方法名称
                    Arrays.asList(new Address(to), uint256),
                    Arrays.asList(new TypeReference<Address>() {
                    }, new TypeReference<Uint256>() {
                    })
            );
            String encodedFunction = FunctionEncoder.encode(function);

            BigInteger gasPrice = getGasPrice();
            BigInteger estimateGas = getEstimateGas(from, to);
            estimateGas = estimateGas.add(new BigInteger("30000"));
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice,
                    estimateGas,
                    contractAddress, encodedFunction);

            Credentials credentials = Credentials.create(prvKey);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            Request<?, EthSendTransaction> ethSendTransactionRequest = web3j.ethSendRawTransaction(hexValue);
            EthSendTransaction send = null;

            send = ethSendTransactionRequest.send();
            if (send.hasError()) {
                System.out.println(send.getError().getMessage() + "_______________________errorMessage");
            }
            String transactionHash = send.getTransactionHash();
            return transactionHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void fun() {
        System.out.println(getSymbolName("0x9D1FA651bF92043F26AfdbCa3a0548983d76aCe5"));
        System.out.println(getBalanceOf("0x9D1FA651bF92043F26AfdbCa3a0548983d76aCe5", "0x59Dd7DfB072c1C80CeEc4d08588A01603C5d3bf0"));
        System.out.println(transaction("0x59Dd7DfB072c1C80CeEc4d08588A01603C5d3bf0",
                "0xb2c3D42e20131313c86a8060d9902889054Dc738",
                "0x9D1FA651bF92043F26AfdbCa3a0548983d76aCe5",
                "302840a7fdeaa5ebee7bffbdf66dbb2ad34b08cfc1c9e3c11c98850273e04f09", "3000"));
    }
}
