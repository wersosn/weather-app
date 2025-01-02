package com.example.projektsm.api;
import com.example.projektsm.api.model.WeatherResponse;
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
}
