package in.lanetbit.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ExchangeMarketHistoryData {


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
    private JsonObject data;



    @SerializedName("data")
    @Expose
    private ArrayList<ExchangeMarketHistoryDataList> marketHistoryDataListArrayList;

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

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }


    public ArrayList<ExchangeMarketHistoryDataList> getMarketHistoryDataListArrayList() {
        return marketHistoryDataListArrayList;
    }

    public void setMarketHistoryDataListArrayList(ArrayList<ExchangeMarketHistoryDataList> marketHistoryDataListArrayList) {
        this.marketHistoryDataListArrayList = marketHistoryDataListArrayList;
    }
}
