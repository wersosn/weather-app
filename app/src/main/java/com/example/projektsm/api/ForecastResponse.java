package com.example.projektsm.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    private List<Forecast> list;

    @SerializedName("city")
    private City city;


    public List<Forecast> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }


    public static class Forecast {
        @SerializedName("dt")
        private long dt;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weather;

        @SerializedName("wind")
        private Wind wind;

        public long getDt() {
            return dt;
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

        public static class Main {
            @SerializedName("temp")
            private double temp;

            @SerializedName("temp_min")
            private double tempMin;

            @SerializedName("temp_max")
            private double tempMax;

            public double getTemp() {
                return temp;
            }

            public double getTempMin() {
                return tempMin;
            }

            public double getTempMax() {
                return tempMax;
            }
        }

        public static class Weather {
            @SerializedName("description")
            private String description;

            @SerializedName("icon")
            private String icon;

            @SerializedName("main")
            private String main;

            public String getDescription() {
                return description;
            }

            public String getIcon() {
                return icon;
            }

            public String getMain() { return main; }
        }

        public static class Wind {
            @SerializedName("speed")
            private double speed;

            public double getSpeed() {
                return speed;
            }
        }
    }

    public static class City {
        @SerializedName("name")
        private String name;

        @SerializedName("country")
        private String country;

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }
    }
}
