package com.example.projektsm.db;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.projektsm.R;
import com.example.projektsm.api.RetrofitClient;
import com.example.projektsm.api.WeatherApiService;
import com.example.projektsm.api.WeatherResponse;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCityActivity extends AppCompatActivity {
    private DataBase db;
    private static final String API_KEY = "68d344c1d7699bddc73ed97ae19f8052";
    private final List<String> polishCities = Arrays.asList(
            "Warszawa", "Kraków", "Gdańsk", "Wrocław", "Poznań",
            "Szczecin", "Katowice", "Lublin", "Bydgoszcz",
            "Olsztyn", "Białystok", "Gdynia", "Radom"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_city);

        db = Room.databaseBuilder(getApplicationContext(),
                DataBase.class, "user_database").allowMainThreadQueries().build();

        EditText cityNameInput = findViewById(R.id.city_name_input);
        Button saveCityButton = findViewById(R.id.save_city_button);

        saveCityButton.setOnClickListener(v -> {
            String cityName = cityNameInput.getText().toString().trim();
            if (!cityName.isEmpty()) {
                validateAndSaveCity(cityName);
            } else {
                Toast.makeText(this, R.string.input, Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView cityRecyclerView = findViewById(R.id.suggested_cities_recycler_view);
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        PolishCitiesAdapter adapter = new PolishCitiesAdapter(polishCities, this::validateAndSaveCity);
        cityRecyclerView.setAdapter(adapter);
    }

    private void validateAndSaveCity(String cityName) {
        WeatherApiService weatherApi = RetrofitClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(cityName, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    City existingCity = db.icity().getCityByName(cityName);
                    if (existingCity == null) {
                        db.icity().insert(new City(cityName));
                        Toast.makeText(AddCityActivity.this, R.string.city_added, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddCityActivity.this, R.string.city_exists_in_db, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Miasto nie istnieje
                    Toast.makeText(AddCityActivity.this, R.string.city_not_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("AddCityActivity", "Błąd API: " + t.getMessage());
                Toast.makeText(AddCityActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
