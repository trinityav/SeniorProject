package com.example.myapplication.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {UserEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}