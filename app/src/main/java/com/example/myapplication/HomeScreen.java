package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;

public class HomeScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setActivityLayout(R.layout.activity_home_screen);

        Button startWorkoutButton = findViewById(R.id.startWorkoutButton);

        if (startWorkoutButton != null) {
            startWorkoutButton.setOnClickListener(v -> {
                startActivity(new Intent(HomeScreen.this, Workout.class));
            });
        }
    }
}
