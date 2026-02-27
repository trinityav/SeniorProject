package com.example.myapplication.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WorkoutSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSessions(List<WorkoutSessionEntity> sessions);

    @Query("SELECT * FROM workout_sessions WHERE planId = :planId")
    List<WorkoutSessionEntity> getSessionsForPlan(long planId);

    @Query("UPDATE workout_sessions SET completed = :completed WHERE sessionId = :sessionId")
    void setSessionCompleted(long sessionId, boolean completed);

    @Update
    void updateSession(WorkoutSessionEntity session);
}