package in.lanetbit.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = ForgotPasswordActivity.class.getName();


    public TextView textViewSubmit;
    private EditText editTextMobileNo;
    private String stringMobileNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = ForgotPasswordActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);

        initiateAllView();
    }

    private void initiateAllView() {
        editTextMobileNo = findViewById(R.id.ed_mobileNo);
        textViewSubmit = findViewById(R.id.tv_send);
       textViewSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send:
                if (editTextMobileNo.getText().toString().isEmpty()){
                    hlp.showToast("Please enter your valid phone no !");
                }else{
                    stringMobileNo = editTextMobileNo.getText().toString();
                    postSendOtpRequest(stringMobileNo);

                }

                break;

        }
    }

    private void postSendOtpRequest(String stringMobileNo) {
        Log.e(TAG, "Calling API");

        Log.e(TAG, "Mobile  : " + stringMobileNo);

        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", stringMobileNo);
            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.forgetPassword(jsonObject);
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
                        hlp.showToast(res.getString("message"));

                        if (res.getString("type").equalsIgnoreCase("success")) {

                            Intent intent = new Intent(mContext, OtpVerifyActivity.class);
                            intent.putExtra("FORGOT_PWD_TYPE","FORGOT_PWD");
                            intent.putExtra("EMAIL",stringMobileNo);
                            startActivity(intent);

                        } else {

                            hlp.showToast(res.getString("message"));
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
            hlp.showToast("mobile number not registerd");


        }

    }
}
