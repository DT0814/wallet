package lr.com.wallet.pojo;

/**
 * Created by DT0814 on 2018/8/28.
 */

public class Price {
    private String symbol;
    private String name;
    private double priceUSD;
    private double priceCNY;
    private double priceBTC;
    private double percentChange7d;
    private double percentChange24h;
    private double percentChange1h;

    public Price() {
    }

    @Override
    public String toString() {
        return "Price{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", priceUSD=" + priceUSD +
                ", priceCNY=" + priceCNY +
                ", priceBTC=" + priceBTC +
                ", percentChange7d=" + percentChange7d +
                ", percentChange24h=" + percentChange24h +
                ", percentChange1h=" + percentChange1h +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceUSD() {
        return priceUSD;
    }

    public void setPriceUSD(double priceUSD) {
        this.priceUSD = priceUSD;
    }

    public double getPriceCNY() {
        return priceCNY;
    }

    public void setPriceCNY(double priceCNY) {
        this.priceCNY = priceCNY;
    }

    public double getPriceBTC() {
        return priceBTC;
    }

    public void setPriceBTC(double priceBTC) {
        this.priceBTC = priceBTC;
    }

    public double getPercentChange7d() {
        return percentChange7d;
    }

    public void setPercentChange7d(double percentChange7d) {
        this.percentChange7d = percentChange7d;
    }

    public double getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(double percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    public double getPercentChange1h() {
        return percentChange1h;
    }

    public void setPercentChange1h(double percentChange1h) {
        this.percentChange1h = percentChange1h;
    }
}
