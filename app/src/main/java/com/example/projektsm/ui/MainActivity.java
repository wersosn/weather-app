package com.example.projektsm.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projektsm.R;
import com.example.projektsm.api.ForecastResponse;
import com.example.projektsm.api.RetrofitClient;
import com.example.projektsm.api.WeatherApiService;
import com.example.projektsm.api.WeatherResponse;
import com.example.projektsm.db.City;
import com.example.projektsm.db.DataBase;
import com.example.projektsm.sensors.LocationActivity;
import com.example.projektsm.db.CityActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private Button locationButtonOn, listButton;
    private List<City> cityList = new ArrayList<>();
    private DataBase db;
    private String cityName, weatherCondition;
    private Notifications notif;
    private UI UI;
    private boolean night;

    /* Zachowanie stanu */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cityName != null) {
            outState.putString("savedCityName", cityName);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cityName = savedInstanceState.getString("savedCityName", null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWeatherData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        notif = new Notifications(this, notificationPermissionLauncher);
        UI = new UI(this);

        // Sprawdzanie czy jest dostęp do Internetu
        if (!isInternetConnected()) {
            showEnableSettingsDialog(getString(R.string.enable_internet), getString(R.string.enable_internet_message), new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
        setViews();

        // Sprawdzenie, czy są dostępne jakieś miasto
        if (savedInstanceState != null) {
            cityName = savedInstanceState.getString("savedCityName", null);
            Log.d(TAG, "Ostatnie miasto: " + cityName);
        }
        else {
            loadCities();
            cityName = getIntent().getStringExtra("city_name");
        }

        // Ustawianie początkowej pogody
        if (cityName == null && !isLocationEnabled() && cityList.isEmpty()) {
            getCurrentWeather("Warsaw", mainLayout);
            getFiveDayForecast("Warsaw");
        } else if (cityName == null && !isLocationEnabled()) {
            getCurrentWeather(cityList.get(0).getName(), mainLayout);
            getFiveDayForecast(cityList.get(0).getName());
        }
        else if(cityName != null) {
            getCurrentWeather(cityName, mainLayout);
            getFiveDayForecast(cityName);
        }
        else if(isLocationEnabled()) {
            location = new LocationActivity(this, this);
            location.requestLocationPermissionAndFetch(this);
            Log.d(TAG, "Lokalizacja: " + location);
        }
    }

    /* Inicjalizacja widoków */
    private void setViews() {
        mainLayout = findViewById(R.id.main_layout);
        locationButtonOn = findViewById(R.id.location_button_on);
        listButton = findViewById(R.id.city_list_button);

        if (isLocationEnabled()) {
            locationButtonOn.setVisibility(View.GONE);
        }

        temperatureTextView = findViewById(R.id.weatherTextView);
        temperatureFeelsLikeTextView = findViewById(R.id.weather_temp_feels_like);
        pressureTextView = findViewById(R.id.pressure);
        humidityTextView = findViewById(R.id.humidity);
        windTextView = findViewById(R.id.wind);
        visibilityTextView = findViewById(R.id.visibility);
        weatherIcon = findViewById(R.id.weather_icon);

        locationButtonOn.setOnClickListener(v -> requestUserLocation());
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
    }

    private void loadCities() {
        db = Room.databaseBuilder(getApplicationContext(),
                DataBase.class, "user_database").allowMainThreadQueries().build();
        cityList.clear();
        cityList.addAll(db.icity().getAllCities());
    }

    private void requestUserLocation() {
        if(!isLocationEnabled()) {
            locationButtonOn.setVisibility(View.VISIBLE);
            showEnableSettingsDialog(getString(R.string.enable_location), getString(R.string.enable_location_message), new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        else {
            locationButtonOn.setVisibility(View.GONE);
            location = new LocationActivity(this, this);
            location.requestLocationPermissionAndFetch(this);
        }
    }

    public void refreshWeatherData() {
        if (cityList.isEmpty()) {
            if (isLocationEnabled() && location != null) {
                location.fetchLocation();
            } else if (cityName == null && !isLocationEnabled()) {
                getCurrentWeather("Warsaw", mainLayout);
                getFiveDayForecast("Warsaw");
            }
        } else {
            if(cityName == null) {
                getCurrentWeather(cityList.get(0).getName(), mainLayout);
                getFiveDayForecast(cityList.get(0).getName());
            }
            else {
                getCurrentWeather(cityName, mainLayout);
                getFiveDayForecast(cityName);
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /* Obsługa lokalizacji */
    @Override
    public void onLocationRetrieved(double latitude, double longitude, String cityName) {
        if (cityName != null && !cityName.isEmpty()) {
            getCurrentWeather(cityName, mainLayout);
            getFiveDayForecast(cityName);
            City existingCity = db.icity().getCityByName(cityName);
            if(existingCity == null) {
                db.icity().insert(new City(cityName));
            }
        } else {
            temperatureTextView.setText(R.string.no_city_name);
            Log.e(TAG, "Nie udało się pobrać miasta (onLocationRetrived)");
        }
    }

    @Override
    public void onLocationError(String errorMessage) {
        Log.e(TAG, "Nie udało się pobrać lokalizacji: " + errorMessage);
        temperatureTextView.setText(R.string.no_location);
    }

    /* Pobieranie pogody */
    private void getCurrentWeather(String city, ConstraintLayout mainLayout) {
        cityName = city;
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
                    night = UI.isNight(sunrise, sunset);
                    weatherCondition = weather.getWeather().get(0).getMain();
                    int backgroundResource = UI.updateBackground(weatherCondition, night);
                    mainLayout.setBackgroundResource(backgroundResource);

                    // Wyświetlanie wartości:
                    temperatureTextView.setText(getString(R.string.temperature_in, weather.getCityName(), temperature));
                    temperatureFeelsLikeTextView.setText(getString(R.string.feels_like_temperature, feelsLikeTemperature));
                    pressureTextView.setText(getString(R.string.pressure, pressure));
                    humidityTextView.setText(getString(R.string.humidity, humidity));
                    windTextView.setText(getString(R.string.wind, windSpeed));
                    visibilityTextView.setText(getString(R.string.visibility, visibility));
                    weatherIcon.setImageResource(UI.getWeatherIcon(iconCode));

                    // Zmiana koloru tekstu w zależności od pogody:
                    int textColor = UI.getTextColor(weatherCondition, night);
                    temperatureTextView.setTextColor(textColor);
                    temperatureFeelsLikeTextView.setTextColor(textColor);
                    pressureTextView.setTextColor(textColor);
                    humidityTextView.setTextColor(textColor);
                    windTextView.setTextColor(textColor);
                    visibilityTextView.setTextColor(textColor);

                    notif.showWeatherNotification(city, String.valueOf(temperature), String.valueOf(feelsLikeTemperature), iconCode);
                } else {
                    Log.e(TAG, "Error: " + response.errorBody());
                    temperatureTextView.setText(R.string.no_weather);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "Błąd połączenia z API: " + t.getMessage());
            }
        });
    }

    // Pobieranie pogody na kolejne 5 dni:
    private void getFiveDayForecast(String city) {
        WeatherApiService weatherApi = RetrofitClient.getClient().create(WeatherApiService.class);
        Call<ForecastResponse> call = weatherApi.getFiveDayForecast(city, API_KEY, "metric");

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForecastResponse forecastResponse = response.body();
                    displayDailyForecast(forecastResponse.getList());

                } else {
                    Log.e(TAG, "Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e(TAG, "Błąd połączenia z API: " + t.getMessage());
            }
        });
    }

    // Wyświetlanie pogody na kolejne 5 dni
    private void displayDailyForecast(List<ForecastResponse.Forecast> forecasts) {
        LinearLayout dailyLayout = findViewById(R.id.daily_forecast_layout);
        dailyLayout.removeAllViews();

        Map<String, List<ForecastResponse.Forecast>> dailyForecastMap = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (ForecastResponse.Forecast forecast : forecasts) {
            String date = sdf.format(new Date(forecast.getDt() * 1000L));
            if (!dailyForecastMap.containsKey(date)) {
                dailyForecastMap.put(date, new ArrayList<>());
            }
            dailyForecastMap.get(date).add(forecast);
        }

        // Przetwarzanie każdego dnia
        for (Map.Entry<String, List<ForecastResponse.Forecast>> entry : dailyForecastMap.entrySet()) {
            String date = entry.getKey();
            List<ForecastResponse.Forecast> dailyForecasts = entry.getValue();
            double avgTemp = 0;
            String iconCode = null;
            for (ForecastResponse.Forecast dailyForecast : dailyForecasts) {
                avgTemp += dailyForecast.getMain().getTemp();
                if (iconCode == null) {
                    iconCode = dailyForecast.getWeather().get(0).getIcon();
                }
            }
            avgTemp /= dailyForecasts.size();

            // Tworzenie układu dla jednego dnia
            LinearLayout dayLayout = new LinearLayout(this);
            dayLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams dayLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dayLayoutParams.setMargins(20, 0, 20, 0);
            dayLayout.setLayoutParams(dayLayoutParams);
            dayLayout.setGravity(Gravity.CENTER);

            // Data
            TextView dateText = new TextView(this);
            dateText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            dateText.setGravity(Gravity.CENTER);
            dateText.setText(UI.getDayOfWeek(date));
            dateText.setTextSize(18);
            dateText.setTypeface(null, Typeface.BOLD);
            dateText.setTextColor(UI.getTextColor(weatherCondition, night));

            // Ikona pogody
            ImageView weatherIcon = new ImageView(this);
            int iconSize = 80;
            weatherIcon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
            int iconResId = UI.getWeatherIconWeek(iconCode);
            weatherIcon.setImageResource(iconResId);

            // Temperatura
            TextView tempText = new TextView(this);
            tempText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tempText.setGravity(Gravity.CENTER);
            tempText.setText(Math.round(avgTemp) + "°C");
            tempText.setTextSize(16);
            tempText.setTextColor(UI.getTextColor(weatherCondition, night));

            dayLayout.addView(dateText);
            dayLayout.addView(weatherIcon);
            dayLayout.addView(tempText);
            dailyLayout.addView(dayLayout);
        }
    }

    /* Popupy */
    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void showEnableSettingsDialog(String title, String message, Intent settingsIntent) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.turn_on, (dialog, which) -> {startActivityForResult(settingsIntent, 0);})
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    /* Powiadomienia */
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, R.string.notif_enabled, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.notif_disabled, Toast.LENGTH_SHORT).show();
                }
            });
}