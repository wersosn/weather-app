package com.example.projektsm.ui;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projektsm.R;
import com.example.projektsm.api.RetrofitClient;
import com.example.projektsm.api.WeatherApiService;
import com.example.projektsm.api.WeatherResponse;
import com.example.projektsm.sensors.LocationActivity;
import com.example.projektsm.db.CityActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationActivity.LocationCallbackInterface {
    private static final String TAG = "MainActivity";
    private static final String API_KEY = "68d344c1d7699bddc73ed97ae19f8052";
    private TextView temperatureTextView, temperatureFeelsLikeTextView, pressureTextView, humidityTextView, windTextView, visibilityTextView;
    private ImageView weatherIcon;
    private ConstraintLayout mainLayout;
    private LocationActivity location;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button locationButton, listButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sprawdzanie czy jest dostęp do Internetu (póki co zawsze, potem zmienić to na pokazywanie się tylko raz)
        if (!isInternetConnected()) {
            showEnableSettingsDialog("Włącz internet", "Aplikacja wymaga połączenia z internetem. Czy chcesz włączyć Wi-Fi?", new Intent(Settings.ACTION_WIFI_SETTINGS));
        }

        // Ustawienie widoków
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.main_layout);
        locationButton = findViewById(R.id.location_button);
        listButton = findViewById(R.id.city_list_button);
        temperatureTextView = findViewById(R.id.weatherTextView);
        temperatureFeelsLikeTextView = findViewById(R.id.weather_temp_feels_like);
        pressureTextView = findViewById(R.id.pressure);
        humidityTextView = findViewById(R.id.humidity);
        windTextView = findViewById(R.id.wind);
        visibilityTextView = findViewById(R.id.visibility);
        weatherIcon = findViewById(R.id.weather_icon);

        locationButton.setOnClickListener(v -> requestUserLocation());
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshWeatherData();
        });

        getCurrentWeather("Warsaw", mainLayout);
    }

    private void requestUserLocation() {
        if(!isLocationEnabled()) {
            locationButton.setVisibility(View.VISIBLE);
            showEnableSettingsDialog("Włącz lokalizację", "Aplikacja wymaga włączenia lokalizacji. Czy chcesz ją włączyć?", new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        else {
            // Inicjalizacja obsługi lokalizacji
            locationButton.setVisibility(View.GONE);
            location = new LocationActivity(this, this);
            location.requestLocationPermissionAndFetch(this);
        }
    }

    private void refreshWeatherData() {
        if (location != null) {
            location.fetchLocation();
        }
        else {
            getCurrentWeather("Warsaw", mainLayout);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLocationRetrieved(double latitude, double longitude, String cityName) {
        if (cityName != null && !cityName.isEmpty()) {
            getCurrentWeather(cityName, mainLayout);
        } else {
            temperatureTextView.setText("Nie udało się pobrać nazwy miasta");
            Log.e(TAG, "Nie udało się pobrać miasta (onLocationRetrived)");
        }
    }

    @Override
    public void onLocationError(String errorMessage) {
        Log.e(TAG, "Nie udało się pobrać lokalizacji: " + errorMessage);
        temperatureTextView.setText("Nie udało się pobrać lokalizacji");
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
                swipeRefreshLayout.setRefreshing(false);
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

    // Popupy:
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void showEnableSettingsDialog(String title, String message, Intent settingsIntent) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Włącz", (dialog, which) -> {startActivityForResult(settingsIntent, 0);})
                .setNegativeButton("Anuluj", (dialog, which) -> {
                    dialog.dismiss();
                    //finish(); // Zamknij aplikację, jeśli użytkownik nie chce włączyć wymaganej funkcji
                })
                .create()
                .show();
    }
}