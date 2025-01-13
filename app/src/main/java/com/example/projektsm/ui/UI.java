package com.example.projektsm.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projektsm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UI {
    private final Context context;

    public UI(Context context) {
        this.context = context;
    }
    public int getWeatherIcon(String iconCode) {
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
                return R.drawable.icon_03d;
            case "04d":
            case "04n":
                return R.drawable.icon_04d;
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

    public int getWeatherIconWeek(String iconCode) {
        switch (iconCode) {
            case "01d":
            case "01n":
                return R.drawable.icon_01d;
            case "02d":
            case "02n":
                return R.drawable.icon_02d;
            case "03d":
            case "03n":
                return R.drawable.icon_03d;
            case "04d":
            case "04n":
                return R.drawable.icon_04d;
            case "09d":
            case "09n":
                return R.drawable.icon_09d;
            case "10d":
            case "10n":
                return R.drawable.icon_10d;
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

    public String getDayOfWeek(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        try {
            Date parsedDate = sdf.parse(date);
            return dayFormat.format(parsedDate);
        } catch (ParseException e) {
            return "";
        }
    }

    public int updateBackground(String weatherCondition, boolean isNight) {
        if (isNight) {
            return R.drawable.night_gradient;
        } else {
            switch (weatherCondition.toLowerCase()) {
                case "clear":
                    return R.drawable.sunny_gradient;
                case "clouds":
                case "rain":
                case "drizzle":
                    return R.drawable.rainy_gradient;
                case "thunderstorm":
                    return R.drawable.storm_gradient;
                case "snow":
                    return R.drawable.snow_gradient;
                default:
                    return R.drawable.rainy_gradient;
            }
        }
    }

    public int getTextColor(String weatherCondition, boolean isNight) {
        Resources resources = context.getResources();
        if (isNight) {
            return resources.getColor(android.R.color.white);
        } else {
            switch (weatherCondition.toLowerCase()) {
                case "thunderstorm":
                    return resources.getColor(android.R.color.primary_text_light);
                case "snow":
                case "clear":
                case "clouds":
                case "rain":
                case "drizzle":
                default:
                    return resources.getColor(android.R.color.black);
            }
        }
    }

    public int getTextColorForForecast(String weatherCondition) {
        Resources resources = context.getResources();
        switch (weatherCondition.toLowerCase()) {
            case "thunderstorm":
                return resources.getColor(android.R.color.primary_text_light);
            case "snow":
                return resources.getColor(android.R.color.black);
            case "clear":
            case "clouds":
            case "rain":
            case "drizzle":
            default:
                return resources.getColor(android.R.color.white);
        }
    }


    public boolean isNight(long sunrise, long sunset) {
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime < sunrise || currentTime > sunset;
    }
}
