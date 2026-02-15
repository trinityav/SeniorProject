package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile extends BaseActivity {

    private Button testWorkouts;
    private Button editProfile;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // This inflates activity_profile.xml into the FrameLayout container in activity_base.xml
        setActivityLayout(R.layout.activity_profile);

        // Apply edge-to-edge padding to the main content view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContent),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        // Initialize buttons
        testWorkouts = findViewById(R.id.btnTestWorkouts);
        editProfile = findViewById(R.id.btnEditProfile);
        logout = findViewById(R.id.btnLogout);

        if (testWorkouts != null) {
            testWorkouts.setOnClickListener(v -> {
                startActivity(new Intent(Profile.this, WorkoutsActivity.class));
            });
        }

        if (editProfile != null) {
            editProfile.setOnClickListener(v -> {
                // Placeholder for Edit Profile functionality
            });
        }

        if (logout != null) {
            logout.setOnClickListener(v -> finish());
        }

        // Note: BottomNavigationView selection and listeners are handled in BaseActivity
    }
}
