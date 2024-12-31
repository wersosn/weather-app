package com.example.projektsm.api.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class WeatherResponse {
    // Zmienne:
    @SerializedName("name")
    private String cityName;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("visibility")
    private int visibility;

    // Gettery:
    public String getCityName() {
        return cityName;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Wind getWind() {
        return wind;
    }

    public int getVisibility() {
        return visibility;
    }

    public static class Main {
        @SerializedName("temp")
        private float temperature;

        @SerializedName("feels_like")
        private float feelsLike;

        @SerializedName("humidity")
        private int humidity;

        @SerializedName("pressure")
        private int pressure;

        public float getTemperature() {
            return temperature;
        }

        public float getFeelsLike() {
            return feelsLike;
        }

        public int getHumidity() {
            return humidity;
        }

        public int getPressure() {
            return pressure;
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

    public static class Wind {
        @SerializedName("speed")
        private float speed;

        public float getSpeed() {
            return speed;
        }
    }
}
