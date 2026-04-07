package com.example.myapplication.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.WorkoutDay;

@Database(
        entities = {
                WorkoutPlanEntity.class,
                WorkoutSessionEntity.class,
                WorkoutDay.class,
                ScheduleEntry.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutPlanDao workoutPlanDao();
    public abstract WorkoutSessionDao workoutSessionDao();
    public abstract WorkoutDayDao workoutDayDao();
    public abstract ScheduleEntryDao scheduleEntryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "fitness_scholars_db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
