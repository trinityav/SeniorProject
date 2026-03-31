package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class UserDetails extends AppCompatActivity {

    EditText etAge, etHeight, etWeight, etGender, etGoal;
    MaterialButton btnFinishSignup, btnGoToSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etGender = findViewById(R.id.etGender);
        etGoal = findViewById(R.id.etGoal);
        btnFinishSignup = findViewById(R.id.btnFinishSignup);
        btnGoToSchedule = findViewById(R.id.btnGoToSchedule);

        btnFinishSignup.setOnClickListener(v -> saveAndProceed());
        btnGoToSchedule.setOnClickListener(v -> saveAndProceed());
    }

    private void saveAndProceed() {
        String age = etAge.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String goal = etGoal.getText().toString().trim();

        if (age.isEmpty() || height.isEmpty() || weight.isEmpty() || gender.isEmpty() || goal.isEmpty()) {
            Toast.makeText(UserDetails.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Using the class name that exists in schedulepreference.java
            Intent intent = new Intent(UserDetails.this, SchedulePreferencesActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
