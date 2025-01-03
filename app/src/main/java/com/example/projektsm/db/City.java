package com.example.projektsm.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Model miasta
@Entity(tableName = "cities")
public class City {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public City(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
