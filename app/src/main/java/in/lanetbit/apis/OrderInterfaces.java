package in.lanetbit.apis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OrderInterfaces {

    @POST("trade/orders")
    Call<JsonElement> postCreateOrders(@Header("Authorization")String stringAuthorization,@Body JsonObject jsonObjectUserPayload);

    @GET("trade/orders")
    Call<JsonElement> getOrdersHistory(@Header("Authorization")String stringAuthorization);
}
