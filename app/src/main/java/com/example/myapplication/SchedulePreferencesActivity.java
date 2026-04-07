package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.ScheduleEntry;
import com.example.myapplication.data.local.ScheduleEntryDao;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class SchedulePreferencesActivity extends AppCompatActivity {

    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
    private EditText etStartTime, etEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_preference);

        cbMonday    = findViewById(R.id.cbMonday);
        cbTuesday   = findViewById(R.id.cbTuesday);
        cbWednesday = findViewById(R.id.cbWednesday);
        cbThursday  = findViewById(R.id.cbThursday);
        cbFriday    = findViewById(R.id.cbFriday);
        cbSaturday  = findViewById(R.id.cbSaturday);
        cbSunday    = findViewById(R.id.cbSunday);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime   = findViewById(R.id.etEndTime);

        MaterialButton btnSaveSchedule = findViewById(R.id.btnSaveSchedule);

        // If editing, pre-load existing schedule
        loadExistingSchedule();

        btnSaveSchedule.setOnClickListener(v -> {
            ArrayList<String> selectedDays = new ArrayList<>();
            if (cbMonday.isChecked())    selectedDays.add("Monday");
            if (cbTuesday.isChecked())   selectedDays.add("Tuesday");
            if (cbWednesday.isChecked()) selectedDays.add("Wednesday");
            if (cbThursday.isChecked())  selectedDays.add("Thursday");
            if (cbFriday.isChecked())    selectedDays.add("Friday");
            if (cbSaturday.isChecked())  selectedDays.add("Saturday");
            if (cbSunday.isChecked())    selectedDays.add("Sunday");

            String startTime = etStartTime.getText().toString().trim();
            String endTime   = etEndTime.getText().toString().trim();

            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Please enter start and end time", Toast.LENGTH_SHORT).show();
                return;
            }

            ScheduleEntryDao dao = AppDatabase.getInstance(this).scheduleEntryDao();

            Executors.newSingleThreadExecutor().execute(() -> {
                // Clear old schedule and save new one
                dao.deleteAll();
                for (String day : selectedDays) {
                    dao.insertOrUpdate(new ScheduleEntry(day, startTime, endTime));
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Schedule saved!", Toast.LENGTH_SHORT).show();

                    // Check if we came from EditSchedule (stats) or signup flow
                    boolean fromEdit = getIntent().getBooleanExtra("fromEdit", false);
                    if (fromEdit) {
                        finish(); // go back to Stats
                    } else {
                        startActivity(new Intent(this, HomeScreen.class));
                        finish();
                    }
                });
            });
        });
    }

    private void loadExistingSchedule() {
        boolean fromEdit = getIntent().getBooleanExtra("fromEdit", false);
        if (!fromEdit) return;

        ScheduleEntryDao dao = AppDatabase.getInstance(this).scheduleEntryDao();
        Executors.newSingleThreadExecutor().execute(() -> {
            java.util.List<ScheduleEntry> entries = dao.getAllEntries();
            if (entries.isEmpty()) return;

            // Use first entry's times (all days share same time window)
            String start = entries.get(0).startTime;
            String end   = entries.get(0).endTime;

            runOnUiThread(() -> {
                etStartTime.setText(start);
                etEndTime.setText(end);
                for (ScheduleEntry entry : entries) {
                    switch (entry.dayOfWeek) {
                        case "Monday":    cbMonday.setChecked(true);    break;
                        case "Tuesday":   cbTuesday.setChecked(true);   break;
                        case "Wednesday": cbWednesday.setChecked(true); break;
                        case "Thursday":  cbThursday.setChecked(true);  break;
                        case "Friday":    cbFriday.setChecked(true);    break;
                        case "Saturday":  cbSaturday.setChecked(true);  break;
                        case "Sunday":    cbSunday.setChecked(true);    break;
                    }
                }
            });
        });
    }
}
