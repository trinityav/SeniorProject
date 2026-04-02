package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// Landing screen for auth
public class LogIn extends AppCompatActivity {

    private Button signUpButton;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If already logged in, skip auth screen
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LogIn.this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogIn.this, SignUpActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogIn.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}