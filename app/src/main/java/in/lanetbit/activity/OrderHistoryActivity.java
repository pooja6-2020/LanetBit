package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import in.lanetbit.R;
import in.lanetbit.adapter.OrderBookListAdapter;
import in.lanetbit.adapter.OrderListHistoryAdapter;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.OrderInterfaces;
import in.lanetbit.model.AskOrderBookHistory;
import in.lanetbit.model.ExchangeInstrumentsData;
import in.lanetbit.model.OrdersHistory;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {


    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    OrderInterfaces apiServiceOrders;

    String TAG = OrderHistoryActivity.class.getName();
    ArrayList<OrdersHistory> ordersHistoryArrayList = new ArrayList<>();
    private String stringSupportedExchangePairs,stringAuthorization,stringPrice,stringCategory,stringTotalAmount;
    OrderListHistoryAdapter orderListHistoryAdapter;
    RecyclerView recyclerViewOrderListHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mContext = OrderHistoryActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiServiceOrders = ApiClient.getClient().create(OrderInterfaces.class);
        ordersHistoryArrayList = new ArrayList<>();

        getOrderHistory();

        recyclerViewOrderListHistory = findViewById(R.id.rv_order_list);
        recyclerViewOrderListHistory.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewOrderListHistory.setHasFixedSize(true);
        findViewById(R.id.img_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void getOrderHistory() {
        hlp.showLoader("Please wait ...");
        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        Call<JsonElement> call = apiServiceOrders.getOrdersHistory(stringAuthorization);

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

                    if (res.getString("code").equalsIgnoreCase("201")) {
                        hlp.showToast(res.getString("type"));
                        JSONArray jsonArrayData = res.getJSONArray("data");
                        for (int i = 0; i < jsonArrayData.length(); i++) {
                            JSONObject jsonObjectOrderHistory = (JSONObject) jsonArrayData.get(i);
                            OrdersHistory ordersHistory = new OrdersHistory();
                            ordersHistory.setAmount(String.valueOf(jsonObjectOrderHistory.get("amount")));
                            ordersHistory.setId(String.valueOf(jsonObjectOrderHistory.get("id")));
                            ordersHistory.setStatus(String.valueOf(jsonObjectOrderHistory.get("status")));
                            ordersHistory.setInstrument(String.valueOf(jsonObjectOrderHistory.get("instrument")));
                            ordersHistory.setCreatedAt(String.valueOf(jsonObjectOrderHistory.get("createdAt")));
                            ordersHistory.setUpdatedAt(String.valueOf(jsonObjectOrderHistory.get("updatedAt")));
                            ordersHistory.setOrderType(String.valueOf(jsonObjectOrderHistory.get("orderType")));
                            ordersHistoryArrayList.add(ordersHistory);
                        }
                        renderOrderListHistory();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });

    }

    private void renderOrderListHistory() {

        if (ordersHistoryArrayList.size() > 0 ) {
            orderListHistoryAdapter = new OrderListHistoryAdapter(mContext,recyclerViewOrderListHistory,ordersHistoryArrayList);
            recyclerViewOrderListHistory.setAdapter(orderListHistoryAdapter);
            orderListHistoryAdapter.notifyDataSetChanged();
            recyclerViewOrderListHistory.setVisibility(View.VISIBLE);

        }else {
            recyclerViewOrderListHistory.setVisibility(View.GONE);
        }


    }
}
