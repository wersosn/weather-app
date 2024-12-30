package com.example.projektsm.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projektsm.R;
import com.example.projektsm.api.RetrofitClient;
import com.example.projektsm.api.WeatherApiService;
import com.example.projektsm.api.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String API_KEY = "68d344c1d7699bddc73ed97ae19f8052";
    private TextView weatherTextView;
    private ImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherTextView = findViewById(R.id.weatherTextView);
        weatherIcon = findViewById(R.id.weather_icon);
        getCurrentWeather("Goniądz");
    }

    private void getCurrentWeather(String city) {
        WeatherApiService weatherApi = RetrofitClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(city, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String temperature = weather.getMain().getTemperature() + "°C";
                    String iconCode = weather.getWeather().get(0).getIcon();
                    weatherTextView.setText("Temperature in " + weather.getCityName() + ": " + temperature);
                    weatherIcon.setImageResource(getWeatherIcon(iconCode));
                } else {
                    Log.e(TAG, "Response failed: " + response.errorBody());
                    weatherTextView.setText("Failed to load weather data.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private int getWeatherIcon(String iconCode) {
        switch (iconCode) {
            case "01d":
                return R.drawable.icon_01d;
            case "01n":
                return R.drawable.icon_01n;
            case "02d":
                return R.drawable.icon_02d;
            case "02n":
                return R.drawable.icon_02n;
            case "03d":
            case "03n":
            case "04d":
            case "04n":
                return R.drawable.icon_03d;
            case "09d":
            case "09n":
                return R.drawable.icon_09d;
            case "10d":
                return R.drawable.icon_10d;
            case "10n":
                return R.drawable.icon_10n;
            case "11d":
            case "11n":
                return R.drawable.icon_11d;
            case "13d":
            case "13n":
                return R.drawable.icon_13d;
            case "50d":
            case "50n":
                return R.drawable.icon_50d;

            default: return R.drawable.icon_01d;
        }
    }

}