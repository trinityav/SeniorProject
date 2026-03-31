package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SchedulePreferencesActivity extends AppCompatActivity {

    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
    private EditText etStartTime, etEndTime;
    private MaterialButton btnSaveSchedule;
    private TextView tvScheduleResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_preference);

        cbMonday = findViewById(R.id.cbMonday);
        cbTuesday = findViewById(R.id.cbTuesday);
        cbWednesday = findViewById(R.id.cbWednesday);
        cbThursday = findViewById(R.id.cbThursday);
        cbFriday = findViewById(R.id.cbFriday);
        cbSaturday = findViewById(R.id.cbSaturday);
        cbSunday = findViewById(R.id.cbSunday);

        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);
        tvScheduleResult = findViewById(R.id.tvScheduleResult);
        btnSaveSchedule.setOnClickListener(v -> {
            ArrayList<String> selectedDays = new ArrayList<>();

            if (cbMonday.isChecked()) selectedDays.add("Monday");
            if (cbTuesday.isChecked()) selectedDays.add("Tuesday");
            if (cbWednesday.isChecked()) selectedDays.add("Wednesday");
            if (cbThursday.isChecked()) selectedDays.add("Thursday");
            if (cbFriday.isChecked()) selectedDays.add("Friday");
            if (cbSaturday.isChecked()) selectedDays.add("Saturday");
            if (cbSunday.isChecked()) selectedDays.add("Sunday");

            String startTime = etStartTime.getText().toString().trim();
            String endTime = etEndTime.getText().toString().trim();

            if (selectedDays.isEmpty()) {
                Toast.makeText(SchedulePreferencesActivity.this, "Please select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }

            if (startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(SchedulePreferencesActivity.this, "Please enter start and end time", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(SchedulePreferencesActivity.this, "Schedule saved", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SchedulePreferencesActivity.this, HomeScreen.class);
            startActivity(intent);
            finish();
        });
    }
}
