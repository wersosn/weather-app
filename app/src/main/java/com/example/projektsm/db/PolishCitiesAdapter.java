package com.example.projektsm.db;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projektsm.R;

import java.util.List;

public class PolishCitiesAdapter extends RecyclerView.Adapter<PolishCitiesAdapter.StringCityViewHolder> {
    private final List<String> cityList;
    private final OnCityClickListener onCityClickListener;

    public PolishCitiesAdapter(List<String> cityList, OnCityClickListener onCityClickListener) {
        this.cityList = cityList;
        this.onCityClickListener = onCityClickListener;
    }

    public interface OnCityClickListener {
        void onCityClick(String cityName);
    }

    @NonNull
    @Override
    public StringCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.polish_city_item, parent, false);
        return new StringCityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringCityViewHolder holder, int position) {
        String cityName = cityList.get(position);
        holder.cityNameTextView.setText(cityName);
        holder.itemView.setOnClickListener(v -> onCityClickListener.onCityClick(cityName));
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    static class StringCityViewHolder extends RecyclerView.ViewHolder {
        TextView cityNameTextView;

        public StringCityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.city_name_text);
        }
    }
}
