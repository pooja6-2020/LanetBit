package in.lanetbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExchangeMarketHistoryDataList {

    @SerializedName("close")
    @Expose
    private String close;

    @SerializedName("high")
    @Expose
    private String high;

    @SerializedName("low")
    @Expose
    private String low;

    @SerializedName("open")
    @Expose
    private String open;
    @SerializedName("volume")
    @Expose
    private String volume;

    @SerializedName("quoteVolume")
    @Expose
    private String quoteVolume;


    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(String quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

}
