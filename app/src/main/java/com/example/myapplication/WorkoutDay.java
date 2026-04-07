package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "workout_days")
public class WorkoutDay {

    @PrimaryKey
    @NonNull
    public String date; // format: "yyyy-MM-dd"  e.g. "2026-04-07"

    public String workoutName;   // e.g. "Chest Day"
    public String notes;         // optional notes
    public boolean completed;    // true = green, false = blue

    public WorkoutDay(@NonNull String date, String workoutName, String notes, boolean completed) {
        this.date = date;
        this.workoutName = workoutName;
        this.notes = notes;
        this.completed = completed;
    }
}