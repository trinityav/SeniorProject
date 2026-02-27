package com.example.myapplication.data.local;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_sessions")
public class WorkoutSessionEntity {

    @PrimaryKey(autoGenerate = true)
    public long sessionId;

    public long planId;            // FK to WorkoutPlanEntity.planId
    public String date;            // "2026-02-26"
    public String time;            // "18:00"
    public String workoutType;     // "upper body", "lower body", etc.
    public String difficulty;      // "beginner", "intermediate", "advanced"
    public boolean completed;      // true when user finishes
}