package in.lanetbit.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.PrefStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanetbitDataIntentServices extends IntentService {
    private static final String ACTION_FOO = "in.lanetbit.services.action.FOO";

    private static final String EXTRA_PARAM1 = "in.lanetbit.services.extra.PARAM1";


    private static final String TAG = "LanetbitDataIntentServices";

    public LanetbitDataIntentServices() {
        super("LanetbitDataIntentServices");
    }

    public static void startActionDownloadFilter(Context context) {
        PrefStorage prefStorage = new PrefStorage(context);
        String stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        Intent intent = new Intent(context, LanetbitDataIntentServices.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, stringAuthorization);
        intent.putExtra(EXTRA_PARAM1, stringAuthorization);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionFoo(param1);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleActionFoo(String authToken) {

        postSet2FARequest(authToken);

    }

    private void postSet2FARequest(String authToken) {
        Log.e("Token?", authToken);

        final PrefStorage prefStorage = new PrefStorage(getApplicationContext());
        AuthInterface apiService = ApiClient.getClient().create(AuthInterface.class);
        Call<JsonElement> call = apiService.postSet2FARequest(authToken);
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
}
