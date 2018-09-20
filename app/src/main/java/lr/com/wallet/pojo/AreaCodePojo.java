package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/9/20.
 */

public class AreaCodePojo {
    private String name;
    private String number;

    public AreaCodePojo(String name, String number) {
        this.name = name;
        this.number = number;
    }

    @Override
    public String toString() {
        return "AreaCodePojo{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
