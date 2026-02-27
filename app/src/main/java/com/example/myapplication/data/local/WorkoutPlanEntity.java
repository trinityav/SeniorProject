package com.example.myapplication.data.local;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_plans")
public class WorkoutPlanEntity {

    @PrimaryKey(autoGenerate = true)
    public long planId;

    public String userId;          // FK to UserEntity.userId (not enforced, just by value)
    public String goal;            // “lose fat”, “gain muscle” etc.
    public String startDate;       // store as ISO string "2026-02-26" for now
    public String endDate;
    public String status;          // "active", "archived"
}