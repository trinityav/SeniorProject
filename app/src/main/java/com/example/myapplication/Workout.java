package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Workout extends AppCompatActivity {

    private int id;
    public Workout() {

        this.id = -1;
    }

    public Workout(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_workouts_content);

        int workoutIdFromIntent = getIntent().getIntExtra("WORKOUT_ID", -1);
        if (workoutIdFromIntent != -1) {
            this.id = workoutIdFromIntent;
        }
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeScreen.class));
                return true;

            } else if (id == R.id.nav_progress) {
                startActivity(new Intent(this, HomeScreen.class));
                return true;

            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, HomeScreen.class));
                return true;
            }
            return false;
        });
    }
}