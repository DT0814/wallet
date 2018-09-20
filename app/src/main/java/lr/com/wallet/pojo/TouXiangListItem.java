package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/9/14.
 */

public class TouXiangListItem {
    private int id;
    private boolean changed;

    @Override
    public String toString() {
        return "TouXiangListItem{" +
                "id=" + id +
                ", changed=" + changed +
                '}';
    }

    public TouXiangListItem(int id, boolean changed) {
        this.id = id;
        this.changed = changed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
