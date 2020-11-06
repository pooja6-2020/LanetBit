package in.lanetbit.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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

public class TwoFAQrCodeActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = TwoFAQrCodeActivity.class.getName();
    private ImageView imageViewQrCode;
    private EditText editTextVerifyCode;
    private TextView textViewConfirm;
    private String stringActivityType,stringTwoFaAuthOtpCode,stringAuthorization;
    private LinearLayout layoutQrCodeUi;
    private boolean isKycVerifyBoolean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_f_a_qr_code);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = TwoFAQrCodeActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);
        Intent intent = getIntent();
        stringActivityType = intent.getStringExtra("SIGN_IN");


        initElements();


    }

    private void initElements() {
        imageViewQrCode = findViewById(R.id.iv_qr_code_);
        editTextVerifyCode =findViewById(R.id.ed_verification_code);
        layoutQrCodeUi = findViewById(R.id.ll_qr_code_ui);
        textViewConfirm = findViewById(R.id.tv_confirm);
        textViewConfirm.setOnClickListener(this);
            if (stringActivityType!=null){

                if (stringActivityType.matches("SIGN_IN")){
                    layoutQrCodeUi.setVisibility(View.GONE);
                }
            }
            else postSet2FARequest();

    }

    private void postSet2FARequest() {
        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();


         apiService = ApiClient.getClient().create(AuthInterface.class);
        Call<JsonElement> call = apiService.postSet2FARequest(stringAuthorization);
        call.enqueue(new Callback<JsonElement>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonElement jsonElement = response.body();
                assert jsonElement != null;
                String body = jsonElement.toString();
                Log.e(TAG, "Api Response 2FA : " + body);

                try {
                    JSONObject responseJson = new JSONObject(body);
                    if (responseJson.getString("type").equalsIgnoreCase("success")) {
                        JSONObject data = responseJson.getJSONObject("data");
                        prefStorage.setValueString(AppConstants.UserPrefKey.TWO_FA_SET_URL, data.getString("url"));
                        if(data.getString("url")!=null){
                            Bitmap QRBit = printQRCode(data.getString("url"));
                            if (QRBit == null){
                                Toast.makeText(mContext, "Unable to generate code!", Toast.LENGTH_SHORT).show();
                            }else {
                                imageViewQrCode.setImageBitmap(QRBit);
                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });

    }

    private Bitmap printQRCode(String stringTwoFaAuthCodeUrl) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(stringTwoFaAuthCodeUrl, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (editTextVerifyCode.getText().toString().isEmpty()){
                    hlp.showToast("Enter the valid code!");
                }else {
                    stringTwoFaAuthOtpCode = editTextVerifyCode.getText().toString();
                    actionVerifyTwoFaOtp(stringTwoFaAuthOtpCode);
                }

                break;
        }

    }

    private void actionVerifyTwoFaOtp(String stringTwoFaAuthOtpCode) {
        Log.e(TAG, "Calling API");
        try {
            stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("token", stringTwoFaAuthOtpCode);

            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.twoFaOtpVerifyRequest(stringAuthorization,jsonObject);
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
                            hlp.showToast(res.getString("message"));
                            isKycVerifyBoolean= Boolean.parseBoolean(prefStorage.getValueString(AppConstants.UserPrefKey.KYC_IS_UPDATED));

                            if (isKycVerifyBoolean==false){
                                Intent intent = new Intent(mContext,KycVerifyIntroActivity.class);
                                startActivity(intent);
                                finish();

                            }else {
                                Intent intent = new Intent(mContext,DashboardActivity.class);
                                startActivity(intent);
                                finish();

                            }
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
