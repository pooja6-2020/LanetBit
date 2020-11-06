package in.lanetbit.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.UserInterface;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    UserInterface apiService;

    String TAG = SettingsActivity.class.getName();
    private Switch aSwitchSecurityTwoFa;
    private boolean aBooleanSecurityTwoFaEnable, isUserActive, isUser2faEnable;
    private String stringAuthorization;
    private LinearLayout layoutChangePassword, layoutContactUs, layoutTermsCondition, layoutfaq, layoutLegal, layoutHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = SettingsActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(UserInterface.class);
        initElements();
        findViewById(R.id.img_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        isUser2faEnable = Boolean.parseBoolean(prefStorage.getValueString(AppConstants.UserPrefKey.USER_IS_2FA_ENABLE));
        aSwitchSecurityTwoFa = findViewById(R.id.sw_security_two_fa);
        aSwitchSecurityTwoFa.setOnCheckedChangeListener(this);
        if (isUser2faEnable == true) {
            aSwitchSecurityTwoFa.setChecked(true);
        } else aSwitchSecurityTwoFa.setChecked(false);

    }

    private void initElements() {
        layoutContactUs = findViewById(R.id.ll_contact_us);
        layoutTermsCondition = findViewById(R.id.ll_terms_condition);
        layoutfaq = findViewById(R.id.ll_faq);
        layoutLegal = findViewById(R.id.ll_legal);
        layoutHelp = findViewById(R.id.ll_help);
        layoutChangePassword = findViewById(R.id.ll_change_password);

        layoutContactUs.setOnClickListener(this);
        layoutTermsCondition.setOnClickListener(this);
        layoutfaq.setOnClickListener(this);
        layoutLegal.setOnClickListener(this);
        layoutHelp.setOnClickListener(this);
        layoutChangePassword.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_contact_us:
                Intent intent = new Intent(mContext, ContactUsActivity.class);
                startActivity(intent);

                break;

            case R.id.ll_terms_condition:
                Intent inten = new Intent(mContext, TermsConditionActivity.class);
                startActivity(inten);

                break;

            case R.id.ll_faq:
                Intent inte = new Intent(mContext, FaQActivity.class);
                startActivity(inte);

                break;

            case R.id.ll_legal:
                Intent in = new Intent(mContext, LegalActivity.class);
                startActivity(in);

                break;

            case R.id.ll_help:
                Intent i = new Intent(mContext, HelpActivity.class);
                startActivity(i);

                break;
            case R.id.ll_change_password:
                Intent i1 = new Intent(mContext, ChangePasswordActivity.class);
                startActivity(i1);

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked == true) {
            aBooleanSecurityTwoFaEnable = true;
            postTwoFaEnableOrNot(aBooleanSecurityTwoFaEnable);
        } else {
            aBooleanSecurityTwoFaEnable = false;
            postTwoFaEnableOrNot(aBooleanSecurityTwoFaEnable);
        }
    }

    private void postTwoFaEnableOrNot(boolean aBooleanSecurityTwoFaEnable) {
        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        isUserActive = Boolean.parseBoolean(prefStorage.getValueString(AppConstants.UserPrefKey.USER_IS_ACTIVE));
        Log.e(TAG, "Calling API");
        try {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("is2FAEnabled", aBooleanSecurityTwoFaEnable);
            jsonObject.addProperty("isActive", isUserActive);
            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.postTwoFaEnableOrNot(stringAuthorization, jsonObject);
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

                        //    hlp.showToast(res.getString("message"));

                        if (res.getString("code").equalsIgnoreCase("200") && res.getString("type").equalsIgnoreCase("success")) {

                            hlp.showToast(res.getString("type"));

                            //  JSONObject data = res.getJSONObject("data");

                        } else if (res.getString("code").equalsIgnoreCase("400")) {
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
