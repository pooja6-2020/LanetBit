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

public class ChangePasswordActivity extends AppCompatActivity {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = ChangePasswordActivity.class.getName();
    private EditText editTextNewPassword,editTextCurrentPassword;
    private TextView textViewSubmit;
    private String stringNewPassword,stringCurrentPassword,stringAuthorization;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mContext = ChangePasswordActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);
        initElements();
    }

    private void initElements() {
        editTextCurrentPassword = findViewById(R.id.ed_current_password);
        editTextNewPassword = findViewById(R.id.ed_new_password);
        textViewSubmit = findViewById(R.id.tv_confirm);
        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                actionChangePassword();
            }
        });

    }

    private void actionChangePassword() {
        if (editTextNewPassword.getText().toString().isEmpty()){
            hlp.showToast("Enter New Password !");
        }else if (editTextCurrentPassword.getText().toString().isEmpty()){
            hlp.showToast("Enter Current Password !");
        }else {
            stringNewPassword = editTextNewPassword.getText().toString();
            stringCurrentPassword= editTextCurrentPassword.getText().toString();
            postChangePassword(stringNewPassword,stringCurrentPassword);

        }
    }

    private void postChangePassword(String stringNewPassword, String stringCurrentPassword) {
        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        Log.e(TAG, "Calling API");
        try {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("newPassword", stringNewPassword);
            jsonObject.addProperty("password", stringCurrentPassword);

            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.phoneChangePassword(stringAuthorization,jsonObject);
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

                            prefStorage.clearStorageData();

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
