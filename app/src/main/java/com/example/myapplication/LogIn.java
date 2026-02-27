package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.repo.FitnessRepository;

public class LogIn extends AppCompatActivity {

    private Button signUpButton;
    private Button loginButton;

    private FitnessRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repository = new FitnessRepository(this);

        // create admin user in background if it does not exist
        new Thread(() -> repository.ensureAdminUserExists()).start();

        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogIn.this, SignUpActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogIn.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}