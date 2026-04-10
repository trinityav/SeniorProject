package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class WorkoutDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        Workout workout = (Workout) getIntent().getSerializableExtra("workout_day");

        TextView tvTitle = findViewById(R.id.tvWorkoutDetailsTitle);
        TextView tvMeta = findViewById(R.id.tvWorkoutMeta);
        TextView tvExercises = findViewById(R.id.tvWorkoutExercises);

        if (workout == null) {
            tvTitle.setText("Workout details");
            tvMeta.setText("No workout selected");
            tvExercises.setText("");
            return;
        }

        tvTitle.setText(capitalize(workout.getDay()) + ", " + workout.getWorkoutName());
        tvMeta.setText("Duration, " + workout.getDuration() + " mins    Difficulty, " + capitalize(workout.getIntensity()));
        tvExercises.setText(formatExercises(workout.getExercises()));
    }

    private String formatExercises(List<String> exercises) {
        if (exercises == null || exercises.isEmpty()) return "No exercises listed";
        StringBuilder builder = new StringBuilder();
        for (String ex : exercises) {
            builder.append("• ").append(ex).append("\n");
        }
        return builder.toString().trim();
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}