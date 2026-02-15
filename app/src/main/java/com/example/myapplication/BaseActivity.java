package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                if (!(this instanceof HomeScreen)) {
                    startActivity(new Intent(this, HomeScreen.class));
                }
                return true;
            } else if (id == R.id.nav_progress) {
                if (!(this instanceof Stats)) {
                    startActivity(new Intent(this, Stats.class));
                }
                return true;
            } else if (id == R.id.nav_profile) {
                if (!(this instanceof Profile)) {
                    startActivity(new Intent(this, Profile.class));
                }
                return true;
            }
            return false;
        });
    }

    protected void setActivityLayout(int layoutResID) {
        ViewGroup container = findViewById(R.id.container);
        if (container != null) {
            getLayoutInflater().inflate(layoutResID, container, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBottomNavigationSelection();
    }

    private void updateBottomNavigationSelection() {
        if (bottomNav != null) {
            if (this instanceof HomeScreen) {
                bottomNav.setSelectedItemId(R.id.nav_home);
            } else if (this instanceof Stats) {
                bottomNav.setSelectedItemId(R.id.nav_progress);
            } else if (this instanceof Profile) {
                bottomNav.setSelectedItemId(R.id.nav_profile);
            }
        }
    }
}
