package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;

public class Profile extends BaseActivity {

    Button testWorkouts;
    Button editProfile;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge if needed
        EdgeToEdge.enable(this);

        // Inflate this page's content into BaseActivity's container
        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.container));

        // Initialize buttons inside the container
        testWorkouts = findViewById(R.id.btnTestWorkouts);
        editProfile = findViewById(R.id.btnEditProfile);
        logout = findViewById(R.id.btnLogout);

        // Test Workouts button
        testWorkouts.setOnClickListener(v -> {
            // Open workouts fragment or activity
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new WorkoutsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Edit Profile button
        editProfile.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Logout button
        logout.setOnClickListener(v -> finish());

        // No need to handle BottomNavigationView here — BaseActivity already handles it
    }
}
