package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvGoSignup;

    private AuthApi.AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        authService = AuthApi.getService(this);

        btnBack = findViewById(R.id.btnBack);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoSignup = findViewById(R.id.tvGoSignup);

        btnBack.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            // This field is now used for username
            String username = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthApi.LoginRequest request = new AuthApi.LoginRequest(username, password);

            authService.login(request).enqueue(new Callback<AuthApi.TokenResponse>() {
                @Override
                public void onResponse(Call<AuthApi.TokenResponse> call, Response<AuthApi.TokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getAccessToken();

                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.saveLoginSession(token, username);

                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthApi.TokenResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Server error. Check connection.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvGoSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}