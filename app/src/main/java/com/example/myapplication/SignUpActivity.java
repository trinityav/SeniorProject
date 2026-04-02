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

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private ImageButton btnBack;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnCreateAccount;
    private TextView tvGoLogin;

    private AuthApi.AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        authService = AuthApi.getService(this);

        btnBack = findViewById(R.id.btnBack);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        btnBack.setOnClickListener(v -> finish());

        tvGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnCreateAccount.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthApi.SignupRequest signupRequest = new AuthApi.SignupRequest(username, password);

            authService.signup(signupRequest).enqueue(new Callback<AuthApi.MessageResponse>() {
                @Override
                public void onResponse(Call<AuthApi.MessageResponse> call, Response<AuthApi.MessageResponse> response) {
                    Log.d(TAG, "Signup response code: " + response.code());

                    if (response.isSuccessful()) {
                        // After signup, log the user in right away so token is saved
                        loginAfterSignup(username, password);
                    } else {
                        String errorText = "Unknown error";
                        try {
                            if (response.errorBody() != null) {
                                errorText = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.e(TAG, "Signup failed: " + errorText);
                        Toast.makeText(SignUpActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthApi.MessageResponse> call, Throwable t) {
                    Log.e(TAG, "Signup request failed", t);
                    Toast.makeText(SignUpActivity.this, "Server error. Check connection.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loginAfterSignup(String username, String password) {
        AuthApi.LoginRequest loginRequest = new AuthApi.LoginRequest(username, password);

        authService.login(loginRequest).enqueue(new Callback<AuthApi.TokenResponse>() {
            @Override
            public void onResponse(Call<AuthApi.TokenResponse> call, Response<AuthApi.TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();

                    SessionManager sessionManager = new SessionManager(SignUpActivity.this);
                    sessionManager.saveLoginSession(token, username);

                    Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, UserDetails.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Signup worked, but auto login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthApi.TokenResponse> call, Throwable t) {
                Log.e(TAG, "Auto login after signup failed", t);
                Toast.makeText(SignUpActivity.this, "Signup worked, but auto login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}