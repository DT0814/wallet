package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/9/10.
 */

public class ETHWallet {
    private int id;
    private String name;
    private String keyStore;
    private String mnemonic;
    private String address;
    private String password;
    private byte[] pubKey;

    @Override
    public String toString() {
        return "ETHWallet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", address='" + address + '\'' +
                ", password='" + password + '\'' +
                ", pubKey='" + pubKey + '\'' +
                ", keyStore='" + keyStore + '\'' +
                '}';
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }
}
