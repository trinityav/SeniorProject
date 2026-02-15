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

        getLayoutInflater().inflate(R.layout.activity_home_screen, findViewById(R.id.container));

        Button startWorkoutButton = findViewById(R.id.startWorkoutButton);

        startWorkoutButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeScreen.this, Workout.class));
        });

    }
}
