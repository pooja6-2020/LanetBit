package in.lanetbit.apis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import in.lanetbit.model.CountryData;
import in.lanetbit.model.CurrenciesData;
import in.lanetbit.model.ExchangeInstrumentsData;
import in.lanetbit.model.OrderBookHistory;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExchangeInterfaces {

    @GET("exchange/instruments")
    Call<ExchangeInstrumentsData> getSupportedInstruments();

    @GET("exchange/instruments/{instrument}/history?")
    Call<JsonElement> getMarketHistoryData(@Path("instrument")String stringSupportedExchangePairs, @Query("startDate")String stringStartDateExchange,@Query("endDate") String stringEndDateExchange,@Query("type") String stringTypeSupportedExchange,@Query("count") String countMarketHistoryData);

    @GET("exchange/instruments/{instrument}/orders")
    Observable<OrderBookHistory> getOrderBookHistory(@Path("instrument")String stringSupportedExchangePairs);

    @GET("exchange/crypto-to-crypto/currencies")
    Call<JsonElement> getCurrenciesList(@Header("Authorization") String stringAuthorization);

    @POST("exchange/crypto-to-crypto/tx")
    Call<JsonElement> postCryptoToCryptoRequest(@Header("Authorization") String stringAuthorization,@Body JsonObject jsonObjectPayload);

    @GET("exchange/crypto-to-crypto/tx")
    Call<JsonElement> getCryptoToCryptoList(@Header("Authorization") String stringAuthorization);
}
