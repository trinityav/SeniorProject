package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WorkoutDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        int workoutId = getIntent().getIntExtra("workout_id", -1);

        TextView tv = findViewById(R.id.tvWorkoutDetailsTitle);
        tv.setText("Workout details screen. ID = " + workoutId);
    }
}
