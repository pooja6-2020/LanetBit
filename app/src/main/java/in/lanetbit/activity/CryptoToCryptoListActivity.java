package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.lanetbit.R;
import in.lanetbit.adapter.CryptoListAdapter;
import in.lanetbit.adapter.OrderListHistoryAdapter;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.ExchangeInterfaces;
import in.lanetbit.apis.OrderInterfaces;
import in.lanetbit.model.CryptoToCryptoList;
import in.lanetbit.model.OrdersHistory;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CryptoToCryptoListActivity extends AppCompatActivity {

    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    ExchangeInterfaces apiService;

    String TAG = CryptoToCryptoListActivity.class.getName();

    CryptoListAdapter cryptoListAdapter;
    RecyclerView recyclerViewCryptoList;

    ArrayList<CryptoToCryptoList> cryptoToCryptoLists = new ArrayList<>();
    private String stringAuthorization;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_to_crypto_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mContext = CryptoToCryptoListActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(ExchangeInterfaces.class);

        getCryptoToCryptoList();

        recyclerViewCryptoList = findViewById(R.id.rv_crypto_to_crypto_list);
        recyclerViewCryptoList.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewCryptoList.setHasFixedSize(true);
        findViewById(R.id.img_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getCryptoToCryptoList() {
        hlp.showLoader("Please wait ...");
        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        Call<JsonElement> call = apiService.getCryptoToCryptoList(stringAuthorization);

        Log.wtf("URL Called", call.request().url() + "");

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
                        JSONArray jsonArrayData = res.getJSONArray("data");
                        for (int i = 0; i < jsonArrayData.length(); i++) {
                            JSONObject jsonObject= (JSONObject) jsonArrayData.get(i);
                            CryptoToCryptoList cryptoToCryptoList = new CryptoToCryptoList();
                            cryptoToCryptoList.setCurrencyFrom(jsonObject.getString("currencyFrom"));
                            cryptoToCryptoList.setCurrencyTo(jsonObject.getString("currencyTo"));
                            cryptoToCryptoList.setMoneyReceived(jsonObject.getString("moneyReceived"));
                            cryptoToCryptoList.setMoneySent(jsonObject.getString("moneySent"));
                            cryptoToCryptoList.setStatus(jsonObject.getString("status"));
                            cryptoToCryptoLists.add(cryptoToCryptoList);
                        }
                        renderCryptoToCryptoList();

                    }
                }catch (Exception e){
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

    private void renderCryptoToCryptoList() {
        if (cryptoToCryptoLists.size() > 0 ) {
            cryptoListAdapter = new CryptoListAdapter(mContext,recyclerViewCryptoList,cryptoToCryptoLists);
            recyclerViewCryptoList.setAdapter(cryptoListAdapter);
            cryptoListAdapter.notifyDataSetChanged();
            recyclerViewCryptoList.setVisibility(View.VISIBLE);

        }else {
            recyclerViewCryptoList.setVisibility(View.GONE);
        }

    }
}
