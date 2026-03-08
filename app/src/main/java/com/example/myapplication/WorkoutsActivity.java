package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsActivity extends BaseActivity {

    private WorkoutsAdapter adapter;
    private final List<Workout> allWorkouts = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityLayout(R.layout.activity_workouts);

        initWorkouts();

        RecyclerView rv = findViewById(R.id.recyclerWorkouts);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            adapter = new WorkoutsAdapter(workout -> openWorkoutDetails(workout.getId()));
            rv.setAdapter(adapter);
            adapter.setWorkouts(allWorkouts);
        }
    }

    private void initWorkouts() {
        allWorkouts.clear();
        for (int i = 1; i <= 10; i++) {
            allWorkouts.add(new Workout(i));
        }
    }

    private void openWorkoutDetails(int workoutId) {
        Intent intent = new Intent(this, WorkoutDetailsActivity.class);
        intent.putExtra("workout_id", workoutId);
        startActivity(intent);
    }
}