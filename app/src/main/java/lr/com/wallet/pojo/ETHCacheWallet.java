package lr.com.wallet.pojo;


import java.math.BigDecimal;

public class ETHCacheWallet {
    private Long id;
    private BigDecimal num;
    private String address;
    private String name;
    private String password;
    private String keystorePath;
    private String mnemonic;
    private String balance = "0";

    public ETHCacheWallet(Long id, String address, String name, String password,
                          String keystorePath, String mnemonic) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.password = password;
        this.keystorePath = keystorePath;
        this.mnemonic = mnemonic;
    }

    public ETHCacheWallet() {
    }

    @Override
    public String toString() {
        return "ETHCacheWallet{" +
                "id=" + id +
                ", num=" + num +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", keystorePath='" + keystorePath + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ETHCacheWallet ethCacheWallet = (ETHCacheWallet) o;
        if (address.equals(ethCacheWallet.getAddress())) {
            return true;
        }
        if (id != null ? !id.equals(ethCacheWallet.id) : ethCacheWallet.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (keystorePath != null ? keystorePath.hashCode() : 0);
        result = 31 * result + (mnemonic != null ? mnemonic.hashCode() : 0);
        return result;
    }

    public BigDecimal getNum() {
        return num;
    }

    public void setNum(BigDecimal num) {
        this.num = num;
    }
}
