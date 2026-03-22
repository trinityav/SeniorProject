package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;

public class Profile extends BaseActivity {

    private boolean settingsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_profile);

        LinearLayout settingsDropdown = findViewById(R.id.settingsDropdown);
        MaterialButton btnSettings = findViewById(R.id.btnSettings);

        btnSettings.setOnClickListener(v -> {
            settingsOpen = !settingsOpen;
            settingsDropdown.setVisibility(settingsOpen ? View.VISIBLE : View.GONE);
            btnSettings.setText(settingsOpen ? "⚙  Settings  ▲" : "⚙  Settings  ▼");
        });

        findViewById(R.id.settingChangeUsername).setOnClickListener(v ->
                Toast.makeText(this, "Change Username ", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.settingChangeEmail).setOnClickListener(v ->
                Toast.makeText(this, "Change Email n", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.settingChangePassword).setOnClickListener(v ->
                Toast.makeText(this, "Change Password ", Toast.LENGTH_SHORT).show()
        );

        Switch switchNotifications = findViewById(R.id.switchNotifications);
        switchNotifications.setOnCheckedChangeListener((btn, isChecked) ->
                Toast.makeText(this,
                        "Notifications " + (isChecked ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show()
        );

        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        MaterialButton btnTestWorkouts = findViewById(R.id.btnTestWorkouts);
        btnTestWorkouts.setOnClickListener(v ->
                startActivity(new Intent(this, WorkoutsActivity.class))
        );

        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logoutUser();
            Intent intent = new Intent(this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}