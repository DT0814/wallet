package lr.com.wallet.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DT0814 on 2018/8/30.
 */

public class Test {
    @org.junit.Test
    public void fun() {
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("123");
        list.add("123");
        list.add("789");
        list.subList(0, 4).forEach(System.out::println);
    }

    @org.junit.Test
    public void fun1() {
        String s = "123123_12321312";
        for (String s1 : s.split("_")) {
            System.out.println(s1);
        }

    }
}
