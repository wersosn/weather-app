package com.example.projektsm.db;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.projektsm.R;

import java.util.ArrayList;
import java.util.List;

public class CityActivity extends AppCompatActivity {
    private DataBase db;
    private CityAdapter cityAdapter;
    private List<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        db = Room.databaseBuilder(getApplicationContext(),
                DataBase.class, "user_database").allowMainThreadQueries().build();

        ListView listView = findViewById(R.id.city_list);
        cityAdapter = new CityAdapter(this, cityList);
        listView.setAdapter(cityAdapter);

        Button addCityButton = findViewById(R.id.add_city_button);
        addCityButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCityActivity.class);
            startActivity(intent);
        });

        loadCities();
    }

    private void loadCities() {
        cityList.clear();
        cityList.addAll(db.icity().getAllCities());
        cityAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCities();
    }
}
