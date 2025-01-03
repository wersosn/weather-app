package com.example.projektsm.db;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.projektsm.R;

public class AddCityActivity extends AppCompatActivity {
    private DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_city);

        db = Room.databaseBuilder(getApplicationContext(),
                DataBase.class, "user_database").allowMainThreadQueries().build();

        EditText cityNameInput = findViewById(R.id.city_name_input);
        Button saveCityButton = findViewById(R.id.save_city_button);

        saveCityButton.setOnClickListener(v -> {
            String cityName = cityNameInput.getText().toString();
            if (!cityName.isEmpty()) {
                db.icity().insert(new City(cityName));
                finish();
            }
        });
    }
}
