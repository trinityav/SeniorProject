package com.example.myapplication.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.myapplication.WorkoutDay;
import java.util.List;

@Dao
public interface WorkoutDayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(WorkoutDay day);

    @Delete
    void delete(WorkoutDay day);

    @Query("SELECT * FROM workout_days WHERE date LIKE :yearMonth || '%'")
    List<WorkoutDay> getDaysForMonth(String yearMonth); // pass "2026-04"

    @Query("SELECT * FROM workout_days WHERE date = :date LIMIT 1")
    WorkoutDay getByDate(String date);

    @Query("SELECT * FROM workout_days ORDER BY date DESC LIMIT 5")
    List<WorkoutDay> getRecentWorkouts();

    @Query("SELECT COUNT(*) FROM workout_days WHERE completed = 1")
    int getTotalCompleted();
}