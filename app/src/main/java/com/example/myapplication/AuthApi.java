package com.example.myapplication;

import android.content.Context;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

// Handles backend calls
public class AuthApi {

    private static final String BASE_URL = "https://seniorproject-backend.onrender.com/";

    public interface AuthService {
        @POST("auth/signup")
        Call<MessageResponse> signup(@Body SignupRequest request);

        @POST("auth/login")
        Call<TokenResponse> login(@Body LoginRequest request);

        // Example protected routes
        @GET("profile/me")
        Call<UserResponse> getProfile();

        @PUT("profile/update")
        Call<MessageResponse> updateProfile(@Body ProfileUpdateRequest request);
    }

    public static AuthService getService(Context context) {
        SessionManager sessionManager = new SessionManager(context);

        // Logs backend requests in Logcat
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Automatically adds Bearer token if user is logged in
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();

            String token = sessionManager.getAccessToken();

            if (token != null && !token.isEmpty()) {
                Request newRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }

            return chain.proceed(originalRequest);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AuthService.class);
    }

    public static class SignupRequest {
        private String username;
        private String password;

        public SignupRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class MessageResponse {
        private String message;

        public String getMessage() {
            return message;
        }
    }

    public static class TokenResponse {
        private String access_token;
        private String token_type;

        public String getAccessToken() {
            return access_token;
        }

        public String getTokenType() {
            return token_type;
        }
    }

    // Example model for GET /profile/me
    public static class UserResponse {
        private int id;
        private String username;
        private Integer age;
        private String schedule;

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public Integer getAge() {
            return age;
        }

        public String getSchedule() {
            return schedule;
        }
    }

    // Example model for PUT /profile/update
    public static class ProfileUpdateRequest {
        private Integer age;
        private String schedule;

        public ProfileUpdateRequest(Integer age, String schedule) {
            this.age = age;
            this.schedule = schedule;
        }
    }
}