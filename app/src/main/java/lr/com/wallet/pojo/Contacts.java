package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/8/31.
 */

public class Contacts {
    private int cid;
    private String name;
    private String address;
    //1.bitCoin 2eth
    private int type;

    public Contacts(String name, String address, int type) {
        this.name = name;
        this.address = address;
        this.type = type;
    }

    public Contacts() {
    }

    @Override
    public String toString() {
        return "Contacts{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", type=" + type +
                '}';
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
