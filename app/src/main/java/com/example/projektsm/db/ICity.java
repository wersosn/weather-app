package com.example.projektsm.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// Interfejs do wstawiania, usuwania i pobierania miast (Data Access Object)
@Dao
public interface ICity {
    @Insert
    void insert(City city);

    @Query("SELECT * FROM cities")
    List<City> getAllCities();

    @Query("SELECT * FROM cities WHERE name = :cityName LIMIT 1")
    City getCityByName(String cityName);

    @Delete
    void delete(City city);
}
