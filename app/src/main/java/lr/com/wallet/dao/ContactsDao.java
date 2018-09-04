package lr.com.wallet.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lr.com.wallet.pojo.Contacts;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.SharedPreferencesUtils;
import lr.com.wallet.utils.Type;

/**
 * Created by DT0814 on 2018/8/31.
 */

public class ContactsDao {
    private static final String sfName = "Contacts";
    private static final String idKey = "idKey";

    public static Contacts getById(int id) {
        String contactsStr = SharedPreferencesUtils.getString(sfName, sfName + "_" + id);
        return JsonUtils.jsonToPojo(contactsStr, Contacts.class);
    }

    public static List<Contacts> getAll() {
        List<Contacts> data = new ArrayList();
        Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
        all.forEach((k, v) -> {
            if (!k.equals(idKey)) {
                data.add(JsonUtils.jsonToPojo(v.toString(), Contacts.class));
            }
        });
        return data;
    }

    public static List<Contacts> getEthContacts() {
        List<Contacts> data = new ArrayList();
        Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
        all.forEach((k, v) -> {
            if (!k.equals(idKey)) {
                Contacts contacts = JsonUtils.jsonToPojo(v.toString(), Contacts.class);
                if (contacts.getType() == Type.ETH_TYPE) {
                    data.add(contacts);
                }
            }
        });
        return data;
    }

    public static List<Contacts> getBitCoinContacts() {
        List<Contacts> data = new ArrayList();
        Map<String, Object> all = SharedPreferencesUtils.getAll(sfName);
        all.forEach((k, v) -> {
            if (!k.equals(idKey)) {
                Contacts contacts = JsonUtils.jsonToPojo(v.toString(), Contacts.class);
                if (contacts.getCid() == Type.BIT_COIN_TYPE) {
                    data.add(contacts);
                }
            }
        });
        return data;
    }

    public static Contacts write(Contacts contacts) {
        contacts.setCid(getId());
        SharedPreferencesUtils.writeString(sfName, sfName + "_" + contacts.getCid(), JsonUtils.objectToJson(contacts));
        return contacts;
    }

    private static int getId() {
        int anInt = SharedPreferencesUtils.getInt(sfName, idKey);
        SharedPreferencesUtils.writeInt(sfName, idKey, ++anInt);
        return anInt;
    }
}
