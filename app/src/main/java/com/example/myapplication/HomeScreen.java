package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);

        Button startWorkoutButton = findViewById(R.id.startWorkoutButton);

        startWorkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, Workout.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
                if (id == R.id.nav_home) {
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
