package com.example.projektsm.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projektsm.R;

import java.util.List;
public class CityAdapter extends ArrayAdapter<City> {
    public CityAdapter(@NonNull Context context, @NonNull List<City> objects) {
        super(context, 0, objects);
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

        return convertView;
    }
}
