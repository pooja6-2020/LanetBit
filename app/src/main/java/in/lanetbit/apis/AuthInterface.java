package in.lanetbit.apis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import in.lanetbit.model.CountryData;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface AuthInterface {

    @POST("auth/login")
    Call<JsonElement> login(@Body JsonObject jsonObject);

    @POST("auth/signup")
    Call<JsonElement> signUp(@Body JsonObject jsonObject);

    @POST("auth/phone_verify")
    Call<JsonElement> phoneVerifyRequest(@Body JsonObject jsonObject);

    @GET("assets/countries.json")
    Call<CountryData> getCountriesData();

    @PUT("auth/forgot/password")
    Call<JsonElement> forgetPassword(@Body JsonObject jsonObject);

    @GET("users/me")
    Call<JsonElement> getProfileDetails(@Header("Authorization") String stringAuthorization);

    @POST("users/2fa")
    Call<JsonElement> postSet2FARequest(@Header("Authorization") String authToken);

    @PUT("users/2fa")
    Call<JsonElement> twoFaOtpVerifyRequest(@Header("Authorization") String stringAuthorization, @Body JsonObject jsonObject);

    @POST("auth/otp/resend")
    Call<JsonElement> resentOtpRequest(@Body JsonObject jsonObject);

    @Multipart
    @POST("users/kyc")
    Call<JsonElement> postKycDoc(@Header("Authorization") String stringAuthorization, @Part MultipartBody.Part docImage,@PartMap LinkedHashMap<String, JsonObject> paramObject);
    @PUT("auth/reset/password")
    Call<JsonElement> phoneResetPassword(@Body JsonObject jsonObject);

    @POST("users/password")
    Call<JsonElement> phoneChangePassword(@Header("Authorization")String stringAuthorization, @Body JsonObject jsonObject);
}
