package com.example.simplecalculator.NetworkUtils;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LatestCurrencyService {

    @GET("latest")
    Call<LatestCurrencyResponse> getLatestCurrency(@Query("access_key") String accessKey, @Query("symbols") String symbols);

}
