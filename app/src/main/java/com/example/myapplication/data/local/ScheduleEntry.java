package com.example.myapplication.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "schedule_entries")
public class ScheduleEntry {

    @PrimaryKey
    @NonNull
    public String dayOfWeek; // "Monday", "Tuesday", etc.

    public String startTime;
    public String endTime;

    public ScheduleEntry(@NonNull String dayOfWeek, String startTime, String endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
