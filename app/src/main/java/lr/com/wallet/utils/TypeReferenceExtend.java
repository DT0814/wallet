package lr.com.wallet.utils;

import org.web3j.abi.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by DT0814 on 2018/8/9.
 */

public class TypeReferenceExtend<T> extends TypeReference {
    @Override
    public Class<T> getClassType() throws ClassNotFoundException {
        Type clsType = getType();

        if (getType() instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) clsType).getRawType();
        } else {
            String s = clsType.toString();
            String[] substring = s.split(" ");
            Class<T> tClass = (Class<T>) Class.forName(substring[1]);
            return tClass;
        }
    }
}
