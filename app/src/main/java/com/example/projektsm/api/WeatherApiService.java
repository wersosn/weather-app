package com.example.projektsm.api;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface WeatherApiService {
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String metric
    );

    @GET("forecast")
    Call<ForecastResponse> getFiveDayForecast(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String metric
    );

}
