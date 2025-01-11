package com.example.projektsm.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.example.projektsm.R;

import java.util.List;
public class CityAdapter extends ArrayAdapter<City> {
    private final Context context;
    private final List<City> cities;

    public CityAdapter(@NonNull Context context, @NonNull List<City> objects) {
        super(context, 0, objects);
        this.context = context;
        this.cities = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.city_item, parent, false);
        }

        City city = getItem(position);
        TextView cityName = convertView.findViewById(R.id.city_name_text);
        cityName.setText(city.getName());

        ImageView deleteButton = convertView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            DataBase db = Room.databaseBuilder(context.getApplicationContext(),
                    DataBase.class, "user_database").allowMainThreadQueries().build();
            db.icity().delete(city);
            cities.remove(position);
            notifyDataSetChanged();
        });

        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        convertView.startAnimation(fadeIn);

        return convertView;
    }
}
