package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsActivity extends AppCompatActivity {

    private View todayView;
    private View allView;

    private WorkoutsAdapter adapter;

    private final List<Workout> allWorkouts = new ArrayList<>();
    private Workout todayWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        initWorkouts();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        FrameLayout tabContent = findViewById(R.id.tabContent);

        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("All Workouts"));

        LayoutInflater inflater = LayoutInflater.from(this);
        todayView = inflater.inflate(R.layout.today_workout_content, tabContent, false);
        allView = inflater.inflate(R.layout.all_workouts_content, tabContent, false);

        tabContent.addView(todayView);
        tabContent.addView(allView);
        allView.setVisibility(View.GONE);

        setupTodayView(todayView);
        setupAllView(allView);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                boolean showToday = tab.getPosition() == 0;
                todayView.setVisibility(showToday ? View.VISIBLE : View.GONE);
                allView.setVisibility(showToday ? View.GONE : View.VISIBLE);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeScreen.class));
                return true;
            }

            if (id == R.id.nav_progress) {
                return true;
            }

            if (id == R.id.nav_profile) {
                return true;
            }

            return false;
        });
    }

    private void initWorkouts() {
        allWorkouts.clear();
        allWorkouts.add(new Workout(1));
        allWorkouts.add(new Workout(2));
        allWorkouts.add(new Workout(3));
        allWorkouts.add(new Workout(4));
        allWorkouts.add(new Workout(5));
        allWorkouts.add(new Workout(6));

        todayWorkout = allWorkouts.get(0);
    }

    private void setupTodayView(View view) {
        Button btnStart = view.findViewById(R.id.btnStartWorkout);
        btnStart.setOnClickListener(v -> openWorkoutDetails(todayWorkout.getId()));
    }

    private void setupAllView(View view) {
        RecyclerView rv = view.findViewById(R.id.rvWorkouts);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WorkoutsAdapter(workout -> openWorkoutDetails(workout.getId()));
        rv.setAdapter(adapter);

        adapter.setWorkouts(allWorkouts);
    }

    private void openWorkoutDetails(int workoutId) {
        Intent intent = new Intent(this, WorkoutDetailsActivity.class);
        intent.putExtra("workout_id", workoutId);
        startActivity(intent);
    }
}
