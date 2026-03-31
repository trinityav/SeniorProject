package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.local.UserEntity;
import com.example.myapplication.data.repo.FitnessRepository;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private ImageButton btnBack;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnCreateAccount;
    private TextView tvGoLogin;

    private FitnessRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_signup_form);

            try {
                repository = new FitnessRepository(this);
            } catch (Exception e) {
                Log.e(TAG, "Error initializing repository", e);
                Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
            }

            btnBack = findViewById(R.id.btnBack);
            etUsername = findViewById(R.id.etUsername);
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            etConfirmPassword = findViewById(R.id.etConfirmPassword);
            btnCreateAccount = findViewById(R.id.btnCreateAccount);
            tvGoLogin = findViewById(R.id.tvGoLogin);

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            if (tvGoLogin != null) {
                tvGoLogin.setOnClickListener(v -> {
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            if (btnCreateAccount != null) {
                btnCreateAccount.setOnClickListener(v -> {
                    if (etUsername == null || etEmail == null || etPassword == null || etConfirmPassword == null) {
                        return;
                    }

                    String username = etUsername.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();

                    if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    } else if (!password.equals(confirmPassword)) {
                        Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    } else if (repository == null) {
                        Toast.makeText(SignUpActivity.this, "Database not available", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(() -> {
                            try {
                                UserEntity existing = repository.getUserByEmail(email);
                                if (existing != null) {
                                    runOnUiThread(() ->
                                            Toast.makeText(SignUpActivity.this, "Email already in use", Toast.LENGTH_SHORT).show()
                                    );
                                } else {
                                    repository.createUser(username, email, password);
                                    UserEntity newUser = repository.getUserByEmail(email);
                                    runOnUiThread(() -> {
                                        Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                        
                                        // Auto-login after signup to maintain session through setup flow
                                        if (newUser != null) {
                                            SessionManager sessionManager = new SessionManager(this);
                                            sessionManager.loginUser(newUser.id);
                                        }

                                        // Redirect to User Details as requested
                                        Intent intent = new Intent(SignUpActivity.this, UserDetails.class);
                                        startActivity(intent);
                                        finish();
                                    });
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error during signup", e);
                                runOnUiThread(() ->
                                        Toast.makeText(SignUpActivity.this, "Signup failed", Toast.LENGTH_SHORT).show()
                                );
                            }
                        }).start();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Critical error in onCreate", e);
            finish();
        }
    }
}