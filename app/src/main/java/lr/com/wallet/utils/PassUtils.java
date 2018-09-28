package lr.com.wallet.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DT0814 on 2018/9/26.
 */

public class PassUtils {
    public static boolean checkPass(String pass) {
        Pattern p1 = Pattern.compile("[0-9]");
        Pattern p2 = Pattern.compile("[a-z]|[A-Z]");
        Matcher m = p1.matcher(pass);
        Matcher m2 = p2.matcher(pass);
        if (m.find() && m2.find() && pass.length() >= 8) {
            return true;
        }
        return false;
    }

    @Test
    public void test() {
        System.out.println(checkPass("1231123213"));
        System.out.println(checkPass("asdadsad"));
        System.out.println(checkPass("123asdasd1123213"));
        System.out.println(checkPass("123a323"));
    }
}
