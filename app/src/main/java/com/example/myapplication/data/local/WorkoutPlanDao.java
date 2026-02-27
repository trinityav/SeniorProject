package com.example.myapplication.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPlan(WorkoutPlanEntity plan);

    @Query("SELECT * FROM workout_plans WHERE userId = :userId AND status = 'active' LIMIT 1")
    WorkoutPlanEntity getActivePlanForUser(String userId);

    @Query("SELECT * FROM workout_plans WHERE userId = :userId")
    List<WorkoutPlanEntity> getAllPlansForUser(String userId);
}