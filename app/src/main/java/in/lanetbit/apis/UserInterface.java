package in.lanetbit.apis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface UserInterface {

    @Multipart
    @PUT("users/me")
    Call<JsonElement> postUpdateUserDetail(@Header("Authorization")String stringAuthorization, @Part MultipartBody.Part docImage,@PartMap LinkedHashMap<String, JSONObject> paramObject);

    @PUT("users/settings")
    Call<JsonElement> postTwoFaEnableOrNot(@Header("Authorization")String stringAuthorization, @Body JsonObject jsonObject);

    @POST("users/bank")
    Call<JsonElement> postUpdateBankUserInfo(@Header("Authorization")String stringAuthorization,@Body JsonObject jsonObjectPayload);
}
