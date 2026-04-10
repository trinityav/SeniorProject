package com.example.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchedulePreferencesActivity extends AppCompatActivity {

    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
    private EditText etStartTime, etEndTime;
    private MaterialButton btnSaveSchedule;
    private AuthApi.AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_preference);

        authService = AuthApi.getService(this);

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

        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));

        btnSaveSchedule.setOnClickListener(v -> saveScheduleAndGeneratePlan());
    }

    private void showTimePicker(EditText target) {
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> target.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                18,
                0,
                true
        );
        dialog.show();
    }

    private void saveScheduleAndGeneratePlan() {
        List<AuthApi.AvailabilityItem> availability = new ArrayList<>();

        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please choose start and end time", Toast.LENGTH_SHORT).show();
            return;
        }

        addIfChecked(availability, cbMonday, "monday", startTime, endTime);
        addIfChecked(availability, cbTuesday, "tuesday", startTime, endTime);
        addIfChecked(availability, cbWednesday, "wednesday", startTime, endTime);
        addIfChecked(availability, cbThursday, "thursday", startTime, endTime);
        addIfChecked(availability, cbFriday, "friday", startTime, endTime);
        addIfChecked(availability, cbSaturday, "saturday", startTime, endTime);
        addIfChecked(availability, cbSunday, "sunday", startTime, endTime);

        if (availability.isEmpty()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveSchedule.setEnabled(false);

        authService.saveAvailability(availability).enqueue(new Callback<AuthApi.MessageResponse>() {
            @Override
            public void onResponse(Call<AuthApi.MessageResponse> call, Response<AuthApi.MessageResponse> response) {
                if (response.isSuccessful()) {
                    generateWorkoutPlan();
                } else {
                    btnSaveSchedule.setEnabled(true);
                    Toast.makeText(SchedulePreferencesActivity.this, "Failed to save schedule", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthApi.MessageResponse> call, Throwable t) {
                btnSaveSchedule.setEnabled(true);
                Toast.makeText(SchedulePreferencesActivity.this, "Server error while saving schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateWorkoutPlan() {
        authService.generateWorkoutPlan().enqueue(new Callback<AuthApi.MessageResponse>() {
            @Override
            public void onResponse(Call<AuthApi.MessageResponse> call, Response<AuthApi.MessageResponse> response) {
                btnSaveSchedule.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(SchedulePreferencesActivity.this, "Workout plan generated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SchedulePreferencesActivity.this, WorkoutsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SchedulePreferencesActivity.this, "Schedule saved, but plan generation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthApi.MessageResponse> call, Throwable t) {
                btnSaveSchedule.setEnabled(true);
                Toast.makeText(SchedulePreferencesActivity.this, "Server error while generating plan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addIfChecked(List<AuthApi.AvailabilityItem> list, CheckBox checkBox, String day, String startTime, String endTime) {
        if (checkBox.isChecked()) {
            list.add(new AuthApi.AvailabilityItem(day, startTime, endTime));
        }
    }
}