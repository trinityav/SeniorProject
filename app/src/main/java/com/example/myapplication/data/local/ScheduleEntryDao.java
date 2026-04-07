package com.example.myapplication.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScheduleEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(ScheduleEntry entry);

    @Query("SELECT * FROM schedule_entries")
    List<ScheduleEntry> getAllEntries();

    @Query("SELECT dayOfWeek FROM schedule_entries")
    List<String> getScheduledDays();

    @Query("SELECT * FROM schedule_entries WHERE dayOfWeek = :day LIMIT 1")
    ScheduleEntry getByDay(String day);

    @Query("DELETE FROM schedule_entries")
    void deleteAll();
}
