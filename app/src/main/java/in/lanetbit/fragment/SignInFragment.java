package in.lanetbit.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.auth0.android.jwt.JWT;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.lanetbit.R;
import in.lanetbit.activity.DashboardActivity;
import in.lanetbit.activity.ForgotPasswordActivity;
import in.lanetbit.activity.KycVerifyIntroActivity;
import in.lanetbit.activity.TwoFAQrCodeActivity;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.services.LanetbitDataIntentServices;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    String TAG = "SignInFragment";

    private EditText editTextEmailId, editTextPassword;
    private TextView textViewSignIn,textViewForgotPassword;
    private String stringUsername, stringPassword;
    AuthInterface apiService;
    private SignInButton mBtnGoogleSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int SIGN_IN_REQUEST_CODE = 1001;
    private LinearLayout layoutGoogleSign;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View window = inflater.inflate(R.layout.signin_fragment, container, false);

        initiateAllView(window);
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);
//        mBtnGoogleSignIn= window.findViewById(R.id.sign_in_button);
        layoutGoogleSign= window.findViewById(R.id.ll_gmail_signin);

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, googleSignInOptions);

        // already signed in user
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        updateUI(account);
      //  mBtnGoogleSignIn.setOnClickListener(this::signIn);
        layoutGoogleSign.setOnClickListener(this::signIn);

        return window;
    }

    private void signIn(View view) {

        Intent singInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent, SIGN_IN_REQUEST_CODE);
    }

    // Sign Out Google User
    /*private void signOut(View view) {

//        mGoogleSignInClient.revokeAccess()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
//                            updateUI(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
//
//                        }else{
//                            Toast.makeText(MainActivity.this, "some error", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        mAuth.signOut();
        updateUI(mAuth.getCurrentUser());

    }*/
    private void updateUI(GoogleSignInAccount account) {

        if (account!= null){
            Toast.makeText(mContext,account.getEmail(),Toast.LENGTH_LONG).show();
        }else {
            //Toast.makeText(mContext,"User is Not logged in ",Toast.LENGTH_LONG).show();
        }
    }

    private void initiateAllView(View window) {

        editTextEmailId = window.findViewById(R.id.ed_email);
        editTextPassword = window.findViewById(R.id.ed_password);
        textViewSignIn = window.findViewById(R.id.tv_signIn);
        textViewForgotPassword = window.findViewById(R.id.tv_forgot_password);
        textViewSignIn.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE){

            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleGoogleSignIn(accountTask);
        }
    }

    private void handleGoogleSignIn(Task<GoogleSignInAccount> accountTask) {
        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
            Toast.makeText(mContext,account.getEmail(),Toast.LENGTH_LONG).show();

        } catch (ApiException e) {
            e.printStackTrace();
            //Toast.makeText(mContext,GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()),Toast.LENGTH_LONG).show();

            Log.d(TAG, "handleGoogleSignIn: Error status code: "+e.getStatusCode());
            Log.d(TAG, "handleGoogleSignIn: Error status message: "
                    +GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signIn:
                actionSignIn();
                break;
            case R.id.tv_forgot_password:
                Intent intent = new Intent(mContext, ForgotPasswordActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void actionSignIn() {
        if (editTextEmailId.getText().toString().isEmpty()) {
            editTextEmailId.setError("This field is required .");
        } else if (editTextPassword.getText().toString().isEmpty()) {
            editTextPassword.setError("This field is required .");

        } else {
            stringUsername = editTextEmailId.getText().toString();
            stringPassword = editTextPassword.getText().toString();
            postSignInRequest(stringUsername,stringPassword);

        }

    }

    private void postSignInRequest(String stringUsername, String stringPassword) {
        Log.e(TAG, "Calling API");
        try {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("email", stringUsername);
            jsonObject.addProperty("password", stringPassword);
            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.login(jsonObject);
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
                        if (res.getString("code").equalsIgnoreCase("401")){
                            hlp.showToast(res.getString("message"));

                        }

                        if (res.getString("code").equalsIgnoreCase("200") && res.getString("type").equalsIgnoreCase("success")) {

                            hlp.showToast(res.getString("type"));

                            JSONObject data = res.getJSONObject("data");
                            Log.e("Token ?",data.getString("token"));

                            prefStorage.setJwtToken(data.getString("token"));
                 //           LanetbitDataIntentServices.startActionDownloadFilter(mContext);
                            JSONObject jsonObjectUser = data.getJSONObject("user");
                            JSONObject jsonObjectKyc = jsonObjectUser.getJSONObject("kyc");
                          //  JSONObject jsonObjectKycInfo = jsonObjectKyc.getJSONObject("kycInfo");
//                            prefStorage.setValueString(AppConstants.UserPrefKey.DOC_TYPE_KYC, jsonObjectKycInfo.getString("docType"));
//                            prefStorage.setValueString(AppConstants.UserPrefKey.DOC_ID_KYC, jsonObjectKycInfo.getString("docId"));
                            prefStorage.setValueString(AppConstants.UserPrefKey.KYC_IS_UPDATED, jsonObjectKyc.getString("isKycUpdated"));
                            prefStorage.setValueString(AppConstants.UserPrefKey.USER_IS_ACTIVE, jsonObjectUser.getString("isActive"));
                            prefStorage.setValueString(AppConstants.UserPrefKey.USER_IS_2FA_ENABLE, jsonObjectUser.getString("is2FAEnabled"));

                            if (Boolean.parseBoolean(prefStorage.getValueString(AppConstants.UserPrefKey.USER_IS_2FA_ENABLE))==true){

                                Intent intent = new Intent(mContext, TwoFAQrCodeActivity.class);
                                intent.putExtra("SIGN_IN","SIGN_IN");
                                startActivity(intent);
                                getActivity().finish();

                            }else {
                                Intent intent = new Intent(mContext, DashboardActivity.class);
                                startActivity(intent);
                                getActivity().finish();


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
