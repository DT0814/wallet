package lr.com.wallet;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * Created by dt0814 on 2018/7/11.
 */

public class Test {

    public static void main(String[] args) throws IOException {
        Web3j web3 = Web3j.build(new HttpService("https://rinkeby.infura.io/c0oGHqQQlq6XJU2kz5DL"));
        Request<?, EthAccounts> ethAccountsRequest = web3.ethAccounts();
        EthAccounts send = ethAccountsRequest.send();
        for (String s : send.getAccounts()) {
            System.out.println(s);
        }
    }
}
