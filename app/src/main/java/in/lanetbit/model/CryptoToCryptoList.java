package in.lanetbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CryptoToCryptoList {

    @SerializedName("currencyFrom")
    @Expose
    private String currencyFrom;

    @SerializedName("currencyTo")
    @Expose
    private String currencyTo;

    @SerializedName("moneyReceived")
    @Expose
    private String moneyReceived;

    @SerializedName("moneySent")
    @Expose
    private String moneySent;

    @SerializedName("status")
    @Expose
    private String status;


    @SerializedName("totalFee")
    @Expose
    private String totalFee;




    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public String getMoneyReceived() {
        return moneyReceived;
    }

    public void setMoneyReceived(String moneyReceived) {
        this.moneyReceived = moneyReceived;
    }

    public String getMoneySent() {
        return moneySent;
    }

    public void setMoneySent(String moneySent) {
        this.moneySent = moneySent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }


}
