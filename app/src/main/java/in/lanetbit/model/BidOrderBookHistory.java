package in.lanetbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BidOrderBookHistory {
    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("price")
    @Expose
    private String price;


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
