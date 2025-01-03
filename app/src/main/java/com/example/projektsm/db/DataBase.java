package com.example.projektsm.db;
import androidx.room.Database;
import androidx.room.RoomDatabase;

// Powiązanie interfejsu z bazą danych
@Database(entities = {City.class}, version = 1)
public abstract class DataBase extends RoomDatabase {
    public abstract ICity icity();
}
