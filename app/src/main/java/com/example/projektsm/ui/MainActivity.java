package com.example.projektsm.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
    private TextView temperatureTextView, temperatureFeelsLikeTextView, pressureTextView, humidityTextView, windTextView, visibilityTextView;
    private ImageView weatherIcon;
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.main_layout);
        temperatureTextView = findViewById(R.id.weatherTextView);
        temperatureFeelsLikeTextView = findViewById(R.id.weather_temp_feels_like);
        pressureTextView = findViewById(R.id.pressure);
        humidityTextView = findViewById(R.id.humidity);
        windTextView = findViewById(R.id.wind);
        visibilityTextView = findViewById(R.id.visibility);
        weatherIcon = findViewById(R.id.weather_icon);
        getCurrentWeather("Warszawa", mainLayout);
    }

    private void getCurrentWeather(String city, ConstraintLayout mainLayout) {
        WeatherApiService weatherApi = RetrofitClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(city, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    // Pobranie wartości:
                    int temperature =  Math.round(weather.getMain().getTemperature());
                    int feelsLikeTemperature = Math.round(weather.getMain().getFeelsLike());
                    int humidity = weather.getMain().getHumidity();
                    int pressure = weather.getMain().getPressure();
                    float windSpeed = weather.getWind().getSpeed();
                    float visibility = weather.getVisibility() / 1000f;
                    String iconCode = weather.getWeather().get(0).getIcon();

                    // Pobieranie wschodu i zachodu słońca:
                    long sunrise = weather.getSys().getSunrise();
                    long sunset = weather.getSys().getSunset();
                    boolean night = isNight(sunrise, sunset);
                    String weatherCondition = weather.getWeather().get(0).getMain();
                    int backgroundResource = updateBackground(weatherCondition, night);
                    mainLayout.setBackgroundResource(backgroundResource);

                    // Wyświetlanie wartości:
                    temperatureTextView.setText("Temperatura w " + weather.getCityName() + ": " + temperature + "°C");
                    temperatureFeelsLikeTextView.setText("Odczuwalna temperatura: " + feelsLikeTemperature + "°C");
                    pressureTextView.setText("Ciśnienie: " + pressure + "hPa");
                    humidityTextView.setText("Wilgotność: " + humidity + "%");
                    windTextView.setText("Wiatr: " + windSpeed + "m/s");
                    visibilityTextView.setText("Widoczność: " + visibility + "km");
                    weatherIcon.setImageResource(getWeatherIcon(iconCode));

                    // Zmiana koloru tekstu w zależności od pogody:
                    int textColor = getTextColor(weatherCondition, night);
                    temperatureTextView.setTextColor(textColor);
                    temperatureFeelsLikeTextView.setTextColor(textColor);
                    pressureTextView.setTextColor(textColor);
                    humidityTextView.setTextColor(textColor);
                    windTextView.setTextColor(textColor);
                    visibilityTextView.setTextColor(textColor);
                } else {
                    Log.e(TAG, "Error: " + response.errorBody());
                    temperatureTextView.setText("Nie udało się załadować pogody");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "Błąd połączenia z API: " + t.getMessage());
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

    private int updateBackground(String weatherCondition, boolean isNight) {
        if (isNight) {
            return R.drawable.night_gradient;
        } else {
            switch (weatherCondition.toLowerCase()) {
                case "clear":
                    return R.drawable.sunny_gradient;
                case "rain":
                case "drizzle":
                    return R.drawable.rainy_gradient;
                case "thunderstorm":
                    return R.drawable.storm_gradient;
                case "clouds":
                case "snow":
                    return R.drawable.snow_gradient;
                default:
                    return R.drawable.rainy_gradient;
            }
        }
    }

    private int getTextColor(String weatherCondition, boolean isNight) {
        if (isNight) {
            return getResources().getColor(android.R.color.white);
        } else {
            switch (weatherCondition.toLowerCase()) {
                case "thunderstorm":
                    return getResources().getColor(android.R.color.primary_text_light);
                case "snow":
                case "clear":
                case "clouds":
                case "rain":
                case "drizzle":
                default:
                    return getResources().getColor(android.R.color.black);
            }
        }
    }


    private boolean isNight(long sunrise, long sunset) {
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime < sunrise || currentTime > sunset;
    }


}