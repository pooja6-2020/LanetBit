package in.lanetbit.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.lanetbit.R;
import in.lanetbit.activity.OtpVerifyActivity;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.model.CountryData;
import in.lanetbit.model.CountryList;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText editTextFullName, editTextEmail, editTextMobileNo, editTextPassword;
    private String stringCountry, stringEmail, stringFullName, stringGender, stringPassword, stringMobileNo;
    private Spinner spinnerCountry;
    private RadioButton radioButtonMale, radioButtonFemale;
    private TextView textViewSignUp;

    private Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    String TAG = "SignUpFragment";
    AuthInterface apiService;
    ArrayList<CountryList> countriesArrayList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View window = inflater.inflate(R.layout.signup_fragment, container, false);

        initiateAllView(window);
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);
        getCountryList();

        return window;


    }

    private void initiateAllView(View window) {
        editTextFullName = window.findViewById(R.id.ed_fullname);
        editTextEmail = window.findViewById(R.id.ed_email);
        editTextPassword = window.findViewById(R.id.ed_password);
        editTextMobileNo = window.findViewById(R.id.ed_mobileNo);
        spinnerCountry = window.findViewById(R.id.spn_country);
//        radioButtonMale = window.findViewById(R.id.rb_male);
//        radioButtonFemale = window.findViewById(R.id.rb_female);
        textViewSignUp = window.findViewById(R.id.tv_signUp);

        spinnerCountry.setOnItemSelectedListener(this);
        textViewSignUp.setOnClickListener(this);

    }

    private void getCountryList() {
            hlp.showLoader("Please wait ...");
            retrofit2.Call<CountryData> call = apiService.getCountriesData();
            Log.wtf("URL Called", call.request().url() + "");
            call.enqueue(new Callback<CountryData>() {
                @Override
                public void onResponse(retrofit2.Call<CountryData> call, Response<CountryData> response) {
                    hlp.hideLoader();
                    if (response.body().getCode().equalsIgnoreCase("200") || response.body().getCode() == "200") {
                        generateCountyList(response.body().getCountryListArrayList());
                        Log.e("List ?",response.body().getCountryListArrayList().get(1).getName());
                    } else
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(retrofit2.Call<CountryData> call, Throwable t) {
                    hlp.hideLoader();
                    hlp.noConnection();
                }
            });
    }

        private void generateCountyList(ArrayList<CountryList> countriesList) {
        for (CountryList countriesItemList : countriesList) {
            CountryList countriesItem = new CountryList();
            countriesItem.setCode(countriesItemList.getCode());
            countriesItem.setDial_code(countriesItemList.getDial_code());
            countriesItem.setName(countriesItemList.getName());
            countriesArrayList.add(countriesItem);
            loadCountryList();
        }
    }
//
    private void loadCountryList() {
        String[] stringCountryList = new String[countriesArrayList.size() + 1];
        stringCountryList[0] = "Select Country";
        int i = 1;
        for (CountryList countriesList : countriesArrayList) {
            stringCountryList[i] = countriesList.getName();
            i++;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item, stringCountryList);
        spinnerCountry.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signUp:
                actionSignUp();
                break;

        }
    }

    private void actionSignUp() {
        if (editTextFullName.getText().toString().trim().isEmpty()) {
            editTextFullName.setError("This field is required .");
            return;
        } else if (editTextEmail.getText().toString().trim().isEmpty()) {
            editTextEmail.setError("This field is required .");
            return;
        } else if (editTextPassword.getText().toString().trim().isEmpty()) {
            editTextPassword.setError("This field is required .");
            return;
        } else if (editTextMobileNo.getText().toString().trim().isEmpty()) {
            editTextMobileNo.setError("This field is required .");
            return;
        } else if (editTextMobileNo.getText().toString().trim().isEmpty()) {
            editTextMobileNo.setError("This field is required .");
            return;
        } else {
            stringEmail = editTextEmail.getText().toString();
            stringFullName = editTextFullName.getText().toString();
            stringPassword = editTextPassword.getText().toString();
            stringMobileNo = editTextMobileNo.getText().toString();
//            if (radioButtonFemale.isChecked()) {
//                stringGender = "F";
//            } else stringGender = "M";

            postSignUpRequest(stringCountry, stringEmail, stringFullName, stringGender, stringPassword, stringMobileNo);

//
//            if (radioButtonFemale.isChecked() || radioButtonMale.isChecked()) {
//
//            }

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void postSignUpRequest(String stringCountry, String stringEmail, String stringFullName, String stringGender, String stringPassword, String stringMobileNo) {
        Log.e(TAG, "Calling API");
        try {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("country", "India");
            jsonObject.addProperty("fullName", stringFullName);
            jsonObject.addProperty("email", stringEmail);
            jsonObject.addProperty("password", stringPassword);
//            jsonObject.addProperty("gender", stringGender);
            jsonObject.addProperty("phone", stringMobileNo);
            jsonObject.addProperty("role", AppConstants.ROLE_CUSTOMER);

            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiService.signUp(jsonObject);
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

                        if (res.getString("code").equalsIgnoreCase("201")) {
//                            JSONObject data = res.getJSONObject("data");
                            hlp.showToast(res.getString("message"));

                            Intent intent = new Intent(mContext, OtpVerifyActivity.class);
                            intent.putExtra("MOBILE_NO",stringMobileNo);
                            startActivity(intent);
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
