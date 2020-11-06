package in.lanetbit.activity;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.services.LanetbitDataIntentServices;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerifyActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = OtpVerifyActivity.class.getName();

    private String stringEmail,stringForgotPwd, stringMobileNo, stringOtpNo, stringEnterOtpNo;
    private TextView textViewConfirmOtp,textViewResentOtp;
    private EditText editTextOneBox, editTextTwoBox, editTextThirdBox, editTextFourthBox,editTextFiveBox;
    StringBuilder stringBuilderOtpCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = OtpVerifyActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);

        Intent intent = getIntent();
        stringForgotPwd = intent.getStringExtra("FORGOT_PWD_TYPE");
        stringEmail = intent.getStringExtra("EMAIL");
        stringMobileNo = intent.getStringExtra("MOBILE_NO");
        stringBuilderOtpCode = new StringBuilder();
        initElements();

    }

    private void initElements() {

        editTextOneBox = findViewById(R.id.ed_first_box);
        editTextTwoBox = findViewById(R.id.ed_second_box);
        editTextThirdBox = findViewById(R.id.ed_third_box);
        editTextFourthBox = findViewById(R.id.ed_four_box);
        editTextFiveBox = findViewById(R.id.ed_five_box);
        textViewResentOtp = findViewById(R.id.tv_resent_otp);


        textViewConfirmOtp = findViewById(R.id.tv_confirm_otp);
        textViewConfirmOtp.setOnClickListener(this);
        textViewResentOtp.setOnClickListener(this);

        editTextOneBox.addTextChangedListener(new GenericTextWatcher(editTextOneBox));
        editTextTwoBox.addTextChangedListener(new GenericTextWatcher(editTextTwoBox));
        editTextThirdBox.addTextChangedListener(new GenericTextWatcher(editTextThirdBox));
        editTextFourthBox.addTextChangedListener(new GenericTextWatcher(editTextFourthBox));
        editTextFiveBox.addTextChangedListener(new GenericTextWatcher(editTextFiveBox));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm_otp:

                if (stringForgotPwd != null) {

                    if(stringBuilderOtpCode.toString().isEmpty()){
                        hlp.showToast("Enter valid OTP !");
                    }else {
                        Intent intent = new Intent(this, PasswordConfirmActivity.class);
                        intent.putExtra("EMAIL_ID",stringEmail);
                        intent.putExtra("OTP",stringBuilderOtpCode.toString());

                        startActivity(intent);
                    }


                } else {

                    registerUserOtpVerification(stringBuilderOtpCode.toString(), stringMobileNo);

                }
                break;
            case R.id.tv_resent_otp:
                if (stringMobileNo!=null){
                    postResentOtp(stringMobileNo);
                }
                break;
        }
    }

    private void postResentOtp(String stringMobileNo) {
        Log.e(TAG, "Calling API");
        try {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("phone", stringMobileNo);

            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.resentOtpRequest(jsonObject);
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

                            //JSONObject data = res.getJSONObject("data");
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

    private void registerUserOtpVerification(String strOtp, String stringMobileNo) {
        Log.e(TAG, "Calling API");
        try {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("otp", strOtp);
            jsonObject.addProperty("phone", stringMobileNo);

            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.phoneVerifyRequest(jsonObject);
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

                            JSONObject data = res.getJSONObject("data");
                            Log.e("Token ?",data.getString("token"));

                            prefStorage.setJwtToken(data.getString("token"));
                            JWT jwtToken = new JWT(data.getString("token"));
                            JSONObject jsonObjectUser = data.getJSONObject("user");
                            JSONObject jsonObjectKyc = jsonObjectUser.getJSONObject("kyc");
                            // start intent service to download all type of watch filter data
                            prefStorage.setValueString(AppConstants.UserPrefKey.KYC_IS_UPDATED, jsonObjectKyc.getString("isKycUpdated"));

                            Intent intent = new Intent(mContext, TwoFAQrCodeActivity.class);
                            startActivity(intent);
                            finish();

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

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        public GenericTextWatcher(View view) {
            this.view = view;

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            stringOtpNo = editable.toString();
            switch (view.getId()) {

                case R.id.ed_first_box:
                    if (stringOtpNo.length() == 1)
                        stringBuilderOtpCode.append(stringOtpNo);
                    editTextTwoBox.requestFocus();
                    break;
                case R.id.ed_second_box:
                    if (stringOtpNo.length() == 1) {
                        editTextThirdBox.requestFocus();
                        stringBuilderOtpCode.append(stringOtpNo);
                    } else if (stringOtpNo.length() == 0)
                        editTextOneBox.requestFocus();
                    break;
                case R.id.ed_third_box:
                    if (stringOtpNo.length() == 1) {
                        editTextFourthBox.requestFocus();
                        stringBuilderOtpCode.append(stringOtpNo);
                    } else if (stringOtpNo.length() == 0)
                        editTextTwoBox.requestFocus();
                    break;
                case R.id.ed_four_box:
                    if (stringOtpNo.length() == 1) {
                        editTextFiveBox.requestFocus();

                        stringBuilderOtpCode.append(stringOtpNo);
                    }else if (stringOtpNo.length() == 0)
                        editTextFiveBox.requestFocus();
                    break;
                case R.id.ed_five_box:
                    if (stringOtpNo.length() == 1)
                        stringBuilderOtpCode.append(stringOtpNo);

                    editTextFiveBox.requestFocus();
                    break;
            }
            Log.e("OTP Value ? ", stringBuilderOtpCode.toString());

        }
    }
}
