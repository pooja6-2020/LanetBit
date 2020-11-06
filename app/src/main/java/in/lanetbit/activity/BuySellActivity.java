package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import id.zelory.compressor.Compressor;
import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.apis.ExchangeInterfaces;
import in.lanetbit.apis.OrderInterfaces;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuySellActivity extends AppCompatActivity implements View.OnClickListener {

    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    OrderInterfaces apiServiceOrders;

    String TAG = BuySellActivity.class.getName();
    private String stringOrderType,stringSupportedExchangePairs,stringAuthorization,stringPrice,stringCategory,stringTotalAmount;
    private TextView textViewSellTitle,textViewBuyTitle,textViewBuy,textViewSell,textViewCurrentAmount;
    private EditText editTextIncreaseAmount,editTextTotalAmount;
    float anIntCurrentPrice,anIntIncrementPrice,anIntTotalAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_sell);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = BuySellActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiServiceOrders = ApiClient.getClient().create(OrderInterfaces.class);

        Intent intent = getIntent();
        stringPrice = intent.getStringExtra("PRICE");
        stringCategory = intent.getStringExtra("CATEGORY");
        stringSupportedExchangePairs = intent.getStringExtra("INSTRUMENT");
        textViewBuy = findViewById(R.id.tv_buy);
        textViewSell = findViewById(R.id.tv_sell);
         textViewBuyTitle = findViewById(R.id.tv_buy_title);
        textViewSellTitle = findViewById(R.id.tv_sell_title);
        if (stringCategory.contains(AppConstants.BUY)){
            textViewSell.setVisibility(View.GONE);
            textViewBuy.setVisibility(View.VISIBLE);
            textViewSellTitle.setVisibility(View.GONE);
            textViewBuyTitle.setVisibility(View.VISIBLE);
            stringOrderType= "Buy";
        }
        if (stringCategory.contains(AppConstants.SELL)){
            textViewBuy.setVisibility(View.GONE);
            textViewSell.setVisibility(View.VISIBLE);
            textViewBuyTitle.setVisibility(View.GONE);
            textViewSellTitle.setVisibility(View.VISIBLE);
            stringOrderType= "Sell";
        }

        initElements();
        findViewById(R.id.img_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initElements() {
        editTextIncreaseAmount= findViewById(R.id.ed_increase_amount);
        editTextTotalAmount= findViewById(R.id.ed_total_amount);
        textViewCurrentAmount= findViewById(R.id.tv_current_amount);
        textViewCurrentAmount.setText("$ "+stringPrice);

        anIntTotalAmount=  Float.parseFloat(stringPrice);
        anIntCurrentPrice =  Float.parseFloat(stringPrice);
        editTextTotalAmount.setText("$ "+anIntTotalAmount);

        editTextIncreaseAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence!=null){

                    String stringIncreasePrice = charSequence.toString();
                    if (stringIncreasePrice.isEmpty()){
                        editTextTotalAmount.setText("$ " + anIntCurrentPrice);
                    }else {
                        anIntIncrementPrice =Float.parseFloat(stringIncreasePrice);
                        anIntTotalAmount = anIntCurrentPrice * anIntIncrementPrice;
                        editTextTotalAmount.setText("$ " + anIntTotalAmount);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0)
                    return;

                String stringIncreasePrice = editable.toString();
                anIntIncrementPrice= Float.parseFloat(stringIncreasePrice);
                anIntTotalAmount = anIntCurrentPrice*anIntIncrementPrice;
                editTextTotalAmount.setText("$ "+anIntTotalAmount);


            }
        });

        textViewBuy.setOnClickListener(this);
        textViewSell.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_buy:
                actionCreateOrder(stringOrderType);
                break;
            case R.id.tv_sell:
                actionCreateOrder(stringOrderType);
                break;
        }
    }

    private void actionCreateOrder(String stringOrderType) {
        stringTotalAmount = editTextTotalAmount.getText().toString();
        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        hlp.showLoader("Please wait ...");
        try {

            JsonObject jsonObjectUserPayload = new JsonObject();
            jsonObjectUserPayload.addProperty("orderType", stringOrderType);
            jsonObjectUserPayload.addProperty("instrument", stringSupportedExchangePairs);
            jsonObjectUserPayload.addProperty("amount", stringTotalAmount);
            Log.e("json_detail?", String.valueOf(jsonObjectUserPayload));


            Call<JsonElement> call = apiServiceOrders.postCreateOrders(stringAuthorization, jsonObjectUserPayload);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    hlp.hideLoader();
                    JsonElement jsonElement = response.body();
                    assert jsonElement != null;
                    String body = jsonElement.toString();
                    Log.e(TAG, "Api Response : " + body);
                    try {

                        JSONObject responseJson = new JSONObject(body);

                        if (responseJson.getString("code").equalsIgnoreCase("201") && responseJson.getString("type").equalsIgnoreCase("success")) {
                            hlp.showToast(responseJson.getString("type"));
                            Intent intent = new Intent(mContext,SuccessfullActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            hlp.showToast(responseJson.getString("message"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    hlp.hideLoader();
                    hlp.noConnection();

                    t.printStackTrace();

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
