package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetails extends AppCompatActivity {

    private EditText etAge;
    private EditText etHeightFeet;
    private EditText etHeightInches;
    private EditText etWeight;
    private EditText etGender;
    private RadioGroup rgDifficulty;
    private MaterialButton btnGoToSchedule;

    private AuthApi.AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        authService = AuthApi.getService(this);

        etAge = findViewById(R.id.etAge);
        etHeightFeet = findViewById(R.id.etHeightFeet);
        etHeightInches = findViewById(R.id.etHeightInches);
        etWeight = findViewById(R.id.etWeight);
        etGender = findViewById(R.id.etGender);
        rgDifficulty = findViewById(R.id.rgDifficulty);
        btnGoToSchedule = findViewById(R.id.btnGoToSchedule);

        btnGoToSchedule.setOnClickListener(v -> saveProfileAndContinue());
    }

    private void saveProfileAndContinue() {
        String ageText = etAge.getText().toString().trim();
        String feetText = etHeightFeet.getText().toString().trim();
        String inchesText = etHeightInches.getText().toString().trim();
        String weightText = etWeight.getText().toString().trim();
        String gender = etGender.getText().toString().trim().toLowerCase();

        int selectedDifficultyId = rgDifficulty.getCheckedRadioButtonId();
        if (ageText.isEmpty() || feetText.isEmpty() || inchesText.isEmpty() || weightText.isEmpty() || gender.isEmpty() || selectedDifficultyId == -1) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        int heightFeet;
        int heightInches;
        int weight;
        try {
            age = Integer.parseInt(ageText);
            heightFeet = Integer.parseInt(feetText);
            heightInches = Integer.parseInt(inchesText);
            weight = Integer.parseInt(weightText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (heightInches < 0 || heightInches > 11) {
            Toast.makeText(this, "Inches should be between 0 and 11", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = findViewById(selectedDifficultyId);
        String fitnessLevel = selectedRadio.getText().toString().trim().toLowerCase();

        AuthApi.ProfileUpdateRequest request = new AuthApi.ProfileUpdateRequest(
                age,
                gender,
                weight,
                heightFeet,
                heightInches,
                fitnessLevel,
                "general_fitness",
                null
        );

        btnGoToSchedule.setEnabled(false);

        authService.updateProfile(request).enqueue(new Callback<AuthApi.MessageResponse>() {
            @Override
            public void onResponse(Call<AuthApi.MessageResponse> call, Response<AuthApi.MessageResponse> response) {
                btnGoToSchedule.setEnabled(true);

                if (response.isSuccessful()) {
                    Intent intent = new Intent(UserDetails.this, SchedulePreferencesActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UserDetails.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthApi.MessageResponse> call, Throwable t) {
                btnGoToSchedule.setEnabled(true);
                Toast.makeText(UserDetails.this, "Server error while saving profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}