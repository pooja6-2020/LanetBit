package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.ExchangeInterfaces;
import in.lanetbit.apis.UserInterface;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankAccountActivity extends AppCompatActivity implements View.OnClickListener {


    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    UserInterface apiService;

    String TAG = BankAccountActivity.class.getName();
    private EditText editTextAccountHolderName, editTextAccountNo, editTextBankName, editTextRoutingNo, editTextSwiftOrBicCode;
    private TextView textViewSubmit,textViewCancel,textViewOk;

    private String stringAuthorization,stringAccountHolderName, stringAccountNo, stringBankName, stringRoutingNo, stringSwiftOrBicCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mContext = BankAccountActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(UserInterface.class);

        initElements();

    }

    private void initElements() {
        editTextAccountHolderName = findViewById(R.id.ed_account_holder_name);
        editTextAccountNo = findViewById(R.id.ed_account_no);
        editTextBankName = findViewById(R.id.ed_bank_name);
        editTextRoutingNo = findViewById(R.id.ed_routing_no);
        editTextSwiftOrBicCode = findViewById(R.id.ed_swift_bic_code);
        textViewSubmit = findViewById(R.id.tv_submit);
        findViewById(R.id.img_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        textViewSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                actionUpdateBankUserInfo();
                break;
        }

    }

    private void actionUpdateBankUserInfo() {
        if (editTextAccountHolderName.getText().toString().trim().isEmpty()) {
            editTextAccountHolderName.setError("This field is required .");
            return;
        } else if (editTextAccountNo.getText().toString().trim().isEmpty()) {
            editTextAccountNo.setError("This field is required .");
            return;
        } else if (editTextBankName.getText().toString().trim().isEmpty()) {
            editTextBankName.setError("This field is required .");
            return;
        } else if (editTextRoutingNo.getText().toString().trim().isEmpty()) {
            editTextRoutingNo.setError("This field is required .");
            return;
        } else if (editTextSwiftOrBicCode.getText().toString().trim().isEmpty()) {
            editTextSwiftOrBicCode.setError("This field is required .");
            return;
        }else {
            stringAccountHolderName = editTextAccountHolderName.getText().toString();
            stringAccountNo = editTextAccountNo.getText().toString();
            stringBankName = editTextBankName.getText().toString();
            stringRoutingNo = editTextRoutingNo.getText().toString();
            stringSwiftOrBicCode = editTextSwiftOrBicCode.getText().toString();

            updateBankUserInfo(stringAccountHolderName,stringAccountNo,stringBankName,stringRoutingNo,stringSwiftOrBicCode);



        }
    }

    private void updateBankUserInfo(String stringAccountHolderName, String stringAccountNo, String stringBankName, String stringRoutingNo, String stringSwiftOrBicCode) {

        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        hlp.showLoader("Please wait ...");
        try {

            JsonObject jsonObjectPayload = new JsonObject();
            jsonObjectPayload.addProperty("accountHolderName", stringAccountHolderName);
            jsonObjectPayload.addProperty("accountNumber", stringAccountNo);
            jsonObjectPayload.addProperty("bankName", stringBankName);
            jsonObjectPayload.addProperty("routingNumber", stringRoutingNo);
            jsonObjectPayload.addProperty("swiftOrBicCode", stringSwiftOrBicCode);
            Log.e("json_detail?", String.valueOf(jsonObjectPayload));


            Call<JsonElement> call = apiService.postUpdateBankUserInfo(stringAuthorization,jsonObjectPayload);
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

                        if (responseJson.getString("code").equalsIgnoreCase("200") && responseJson.getString("type").equalsIgnoreCase("success")) {
                            hlp.showToast(responseJson.getString("type"));
                            Intent i= new Intent(mContext,BankAccountActivity.class);
                            startActivity(i);
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
