package lr.com.wallet.test;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试创建钱包地址
 */
public class test {

    public static void main(String[] args){


        long t1=System.currentTimeMillis();
        seedToPasswd(makeSeed(getWord()));;
        long t2=System.currentTimeMillis();
        System.out.println("耗时:"+(t2-t1));

    }


    private static List<String> getWord(){
        StringBuilder sb=new StringBuilder();
        byte[] entropy=new byte[Words.TWELVE.byteLength()];

        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE)
                .createMnemonic(entropy,sb::append);
        System.out.println("助记词："+sb);
        List<String> words=new ArrayList<>();
        String[] arr=sb.toString().split(" ");

        for(String str:arr){
            words.add(str);
        }
        return words;
    }

    private static byte[] makeSeed(List<String> words){
        byte[] seed=new SeedCalculator()
                .withWordsFromWordList(English.INSTANCE)
                .calculateSeed(words,"");

        return seed;

    }

    public static void seedToPasswd(byte[] seeds){

        //这个SHA256可以使用 scryt-1.4.0
        ECKeyPair ecKeyPair=ECKeyPair.create(SHA256Util.getSHA256StrJava(seeds));
        String privateKey=ecKeyPair.getPrivateKey().toString(16);
        String publicKey=ecKeyPair.getPublicKey().toString(16);

        System.out.println("私钥:"+privateKey);
        System.out.println("公钥:"+publicKey);

        try {
            WalletFile walletFile = Wallet.create("123456", ecKeyPair,2,1);
            System.out.println("钱包地址:"+walletFile.getAddress());

        } catch (CipherException e) {
            e.printStackTrace();
        }

    }
}