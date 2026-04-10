package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutsActivity extends BaseActivity {

    private WorkoutsAdapter adapter;
    private final List<Workout> allWorkouts = new ArrayList<>();
    private AuthApi.AuthService authService;
    private TextView tvWorkoutsTitle;
    private MaterialButton btnAddWorkout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityLayout(R.layout.activity_workouts);

        authService = AuthApi.getService(this);

        tvWorkoutsTitle = findViewById(R.id.tvWorkoutsTitle);
        btnAddWorkout = findViewById(R.id.btnAddWorkout);

        RecyclerView rv = findViewById(R.id.recyclerWorkouts);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WorkoutsAdapter(this::openWorkoutDetails);
        rv.setAdapter(adapter);

        if (btnAddWorkout != null) {
            btnAddWorkout.setText("Refresh Plan");
            btnAddWorkout.setOnClickListener(v -> loadWorkoutPlan());
        }

        loadWorkoutPlan();
    }

    private void loadWorkoutPlan() {
        authService.getWorkoutPlan().enqueue(new Callback<AuthApi.WorkoutPlanResponse>() {
            @Override
            public void onResponse(Call<AuthApi.WorkoutPlanResponse> call, Response<AuthApi.WorkoutPlanResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getPlan() != null) {
                    allWorkouts.clear();

                    for (AuthApi.WorkoutPlanDay day : response.body().getPlan()) {
                        List<String> exerciseNames = new ArrayList<>();

                        if (day.getExercises() != null) {
                            for (AuthApi.ExerciseItem ex : day.getExercises()) {
                                String line = ex.getExerciseName();
                                if (ex.getSets() != null && ex.getReps() != null) {
                                    line += " , " + ex.getSets() + " sets x " + ex.getReps();
                                }
                                exerciseNames.add(line);
                            }
                        }

                        int duration = day.getDuration() != null ? day.getDuration() : 30;

                        allWorkouts.add(new Workout(
                                day.getDay(),
                                day.getWorkout(),
                                day.getIntensity(),
                                duration,
                                exerciseNames
                        ));
                    }

                    adapter.setWorkouts(allWorkouts);

                    if (tvWorkoutsTitle != null && !allWorkouts.isEmpty()) {
                        tvWorkoutsTitle.setText("Your Workout Plan");
                    }
                } else {
                    Toast.makeText(WorkoutsActivity.this, "No workout plan found", Toast.LENGTH_SHORT).show();
                    adapter.setWorkouts(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<AuthApi.WorkoutPlanResponse> call, Throwable t) {
                Toast.makeText(WorkoutsActivity.this, "Failed to load workout plan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openWorkoutDetails(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailsActivity.class);
        intent.putExtra("workout_day", workout);
        startActivity(intent);
    }
}