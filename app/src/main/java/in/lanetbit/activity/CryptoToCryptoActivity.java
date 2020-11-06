package in.lanetbit.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.ExchangeInterfaces;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CryptoToCryptoActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    ExchangeInterfaces apiService;

    String TAG = CryptoToCryptoActivity.class.getName();
    private String stringAddress,stringAmount,stringToCurrencies,stringAuthorization, stringFromCurrencies;
    private Spinner spinnerCurrenciesFrom,spinnerCurrenciesTo;
    JSONArray jsonArrayCurrencies;
    private TextView textViewSubmit;
    private EditText editTextAmount,editTextAddress;
    private ImageView imageViewCryptoToCryptoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_to_crypto);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = CryptoToCryptoActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(ExchangeInterfaces.class);
        initElements();

        getCurrenciesList();
        findViewById(R.id.img_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initElements() {
        imageViewCryptoToCryptoList = findViewById(R.id.iv_crypto_to_crypto_list);
        spinnerCurrenciesFrom = findViewById(R.id.spn_currencies_from);
        spinnerCurrenciesFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {
                    try {
                        stringFromCurrencies = String.valueOf(jsonArrayCurrencies.get(position - 1));
                        hlp.showToast(stringFromCurrencies);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "From ? : " + stringFromCurrencies);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerCurrenciesTo = findViewById(R.id.spn_currencies_to);
        spinnerCurrenciesTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {
                    try {
                        stringToCurrencies = String.valueOf(jsonArrayCurrencies.get(position - 1));
                        hlp.showToast(stringToCurrencies);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "To ? : " + stringToCurrencies);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        textViewSubmit =findViewById(R.id.tv_submit);
        editTextAmount =findViewById(R.id.ed_amount);
        editTextAddress =findViewById(R.id.ed_address);
        textViewSubmit.setOnClickListener(this);
        imageViewCryptoToCryptoList.setOnClickListener(this);
    }

    private void getCurrenciesList() {

        hlp.showLoader("Please wait ...");
        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();

        retrofit2.Call<JsonElement> call = apiService.getCurrenciesList(stringAuthorization);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                hlp.hideLoader();
                JsonElement jsonElement = response.body();
                assert jsonElement != null;
                String body = jsonElement.toString();
                Log.e(TAG, "Api Response : " + body);

                try {
                    JSONObject res = new JSONObject(body);

                    if (res.getString("code").equalsIgnoreCase("200")) {
                        hlp.showToast(res.getString("type"));
                        jsonArrayCurrencies = res.getJSONArray("data");
                        Log.e(TAG, "array ? : " + jsonArrayCurrencies);

                        String[] stringCurrenciesFromList = new String[jsonArrayCurrencies.length() + 1];
                        String[] stringCurrenciesToList = new String[jsonArrayCurrencies.length() + 1];
                        stringCurrenciesFromList[0] = "Select From";
                        stringCurrenciesToList[0] = "Select To";
                        //int i = 1;
                        for (int i = 1; i <= jsonArrayCurrencies.length(); i++) {
                            stringCurrenciesFromList[i] = jsonArrayCurrencies.get(i - 1).toString();
                            stringCurrenciesToList[i] = jsonArrayCurrencies.get(i - 1).toString();

                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                                R.layout.spinner_item, stringCurrenciesFromList);
                        ArrayAdapter<String> dataAdapterTo = new ArrayAdapter<String>(mContext,
                                R.layout.spinner_item, stringCurrenciesToList);

                        spinnerCurrenciesFrom.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();

                        spinnerCurrenciesTo.setAdapter(dataAdapterTo);
                        dataAdapterTo.notifyDataSetChanged();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                hlp.hideLoader();
                hlp.noConnection();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                actionPostCryptoToCryptoRequest();
                break;
            case R.id.iv_crypto_to_crypto_list:
                Intent intent = new Intent(mContext, CryptoToCryptoListActivity.class);
                startActivity(intent);
                break;

        }

    }

    private void actionPostCryptoToCryptoRequest() {
    if (spinnerCurrenciesFrom.getSelectedItemPosition()>=1){
        if (spinnerCurrenciesTo.getSelectedItemPosition()>=1){
            if (editTextAmount.getText().toString().isEmpty()){
                hlp.showToast("Please Enter required field ?");

            }else {
                stringAmount = editTextAmount.getText().toString();
            //    stringAddress= editTextAddress.getText().toString();
                postCryptoToCryptoRequest(stringFromCurrencies,stringToCurrencies,stringAmount,stringAddress);
            }
        }else hlp.showToast("Select From Currencies!");
    }else hlp.showToast("Select From Currencies!");
    }

    private void postCryptoToCryptoRequest(String stringFromCurrencies, String stringToCurrencies, String stringAmount, String stringAddress) {
        try {
            stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();

            JsonObject jsonObjectPayload = new JsonObject();
            jsonObjectPayload.addProperty("from", stringFromCurrencies);
            jsonObjectPayload.addProperty("to", stringToCurrencies);
            jsonObjectPayload.addProperty("amount", stringAmount);
            jsonObjectPayload.addProperty("address", "test");
            Call<JsonElement> call =
                    apiService.postCryptoToCryptoRequest(stringAuthorization,jsonObjectPayload);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    hlp.hideLoader();
                    JsonElement jsonElement = response.body();
                    assert jsonElement != null;
                    String body = jsonElement.toString();
                    Log.e(TAG, "Api Response : " + body);

                    try {
                        JSONObject res = new JSONObject(body);
                        hlp.showToast(res.getString("type"));

                        if (res.getString("code").equalsIgnoreCase("201")) {
                            hlp.showToast(res.getString("type"));
                            Intent i= new Intent(mContext,CryptoToCryptoListActivity.class);
                            startActivity(i);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    hlp.hideLoader();
                    hlp.noConnection();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
