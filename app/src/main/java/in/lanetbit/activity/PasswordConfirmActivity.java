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

import com.auth0.android.jwt.JWT;
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

public class PasswordConfirmActivity extends AppCompatActivity {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = PasswordConfirmActivity.class.getName();
    private EditText editTextNewPassword, editTextConfirmPassword;
    private TextView textViewSubmit;
    private String stringChangePassword, stringOtp, stringEmailId, stringNewPassword, stringConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirm);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mContext = PasswordConfirmActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);
        initElements();
        Intent intent = getIntent();
        stringEmailId = intent.getStringExtra("EMAIL_ID");
        stringOtp = intent.getStringExtra("OTP");

        stringChangePassword = intent.getStringExtra("CHANGE_PWD_TYPE");
    }

    private void initElements() {
        editTextConfirmPassword = findViewById(R.id.ed_confirm_password);
        editTextNewPassword = findViewById(R.id.ed_new_password);
        textViewSubmit = findViewById(R.id.tv_confirm);
        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                actionResetPassword();
            }
        });


    }

    private void actionResetPassword() {
        if (editTextNewPassword.getText().toString().isEmpty()) {
            hlp.showToast("Enter New Password !");
        } else if (editTextConfirmPassword.getText().toString().isEmpty()) {
            hlp.showToast("Enter Confirm Password !");
        } else {
            stringNewPassword = editTextNewPassword.getText().toString();
            stringConfirmPassword = editTextConfirmPassword.getText().toString();

            if (stringNewPassword.matches(stringConfirmPassword)) {
                postResetPassword(stringConfirmPassword);
            } else hlp.showToast("Enter Confirm Password");

        }
    }

    private void postResetPassword(String stringConfirmPassword) {
        Log.e(TAG, "Calling API");
        try {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("email", stringEmailId);
            jsonObject.addProperty("otp", stringOtp);
            jsonObject.addProperty("password", stringConfirmPassword);

            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.phoneResetPassword(jsonObject);
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

                        if (res.getString("code").equalsIgnoreCase("200") && res.getString("type").equalsIgnoreCase("success")) {

                            hlp.showToast(res.getString("type"));


                            Intent intent = new Intent(mContext, AuthActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else if (res.getString("code").equalsIgnoreCase("401")) {
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
        }
    }
}
