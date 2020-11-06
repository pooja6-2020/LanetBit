package in.lanetbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CurrenciesData {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private ArrayList<CurrenciesList> currenciesListArrayList;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<CurrenciesList> getCurrenciesListArrayList() {
        return currenciesListArrayList;
    }

    public void setCurrenciesListArrayList(ArrayList<CurrenciesList> currenciesListArrayList) {
        this.currenciesListArrayList = currenciesListArrayList;
    }

}
