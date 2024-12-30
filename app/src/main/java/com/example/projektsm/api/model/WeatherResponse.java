package com.example.projektsm.api.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class WeatherResponse {
    @SerializedName("name")
    private String cityName;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    public String getCityName() {
        return cityName;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public static class Main {
        @SerializedName("temp")
        private float temperature;

        public float getTemperature() {
            return temperature;
        }
    }

    public static class Weather {
        @SerializedName("icon")
        private String icon;

        @SerializedName("description")
        private String description;

        public String getIcon() {
            return icon;
        }

        public String getDescription() {
            return description;
        }
    }
}
