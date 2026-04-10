package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreen extends BaseActivity {

    private static final String[] GREETINGS = {
            "Lets check your activity",
            "You're doing great today",
            "Every rep counts",
            "Keep going",
            "Consistency matters",
            "Stay on track"
    };

    private AuthApi.AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setActivityLayout(R.layout.activity_home_screen);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        authService = AuthApi.getService(this);

        TextView greetingText = findViewById(R.id.greetingText);
        String username = sessionManager.getUsername();
        if (greetingText != null) {
            String line = GREETINGS[new Random().nextInt(GREETINGS.length)];
            greetingText.setText("Hi, " + username + "\n" + line);
        }

        TextView tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        TextView tvUpcomingDate = findViewById(R.id.tvUpcomingDate);
        TextView tvUpcomingWorkoutName = findViewById(R.id.tvUpcomingWorkoutName);

        loadProgress(tvTotalWorkouts);
        loadWorkoutPlanPreview(tvUpcomingDate, tvUpcomingWorkoutName);

        Button startWorkoutButton = findViewById(R.id.startWorkoutButton);
        if (startWorkoutButton != null) {
            startWorkoutButton.setOnClickListener(v ->
                    startActivity(new Intent(HomeScreen.this, WorkoutsActivity.class))
            );
        }

        CardView aiChatCard = findViewById(R.id.aiChatCard);
        CardView upcomingWorkoutCard = findViewById(R.id.upcomingWorkoutCard);
    }

    private void loadProgress(TextView tvTotalWorkouts) {
        authService.getProgress().enqueue(new Callback<AuthApi.ProgressResponse>() {
            @Override
            public void onResponse(Call<AuthApi.ProgressResponse> call, Response<AuthApi.ProgressResponse> response) {
                if (response.isSuccessful() && response.body() != null && tvTotalWorkouts != null) {
                    Integer total = response.body().getTotalWorkouts();
                    tvTotalWorkouts.setText(String.valueOf(total == null ? 0 : total));
                }
            }

            @Override
            public void onFailure(Call<AuthApi.ProgressResponse> call, Throwable t) {
            }
        });
    }

    private void loadWorkoutPlanPreview(TextView tvUpcomingDate, TextView tvUpcomingWorkoutName) {
        authService.getWorkoutPlan().enqueue(new Callback<AuthApi.WorkoutPlanResponse>() {
            @Override
            public void onResponse(Call<AuthApi.WorkoutPlanResponse> call, Response<AuthApi.WorkoutPlanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AuthApi.WorkoutPlanDay> plan = response.body().getPlan();
                    if (plan != null && !plan.isEmpty()) {
                        AuthApi.WorkoutPlanDay first = plan.get(0);
                        if (tvUpcomingDate != null) {
                            tvUpcomingDate.setText(capitalize(first.getDay()));
                        }
                        if (tvUpcomingWorkoutName != null) {
                            tvUpcomingWorkoutName.setText(first.getWorkout());
                        }
                        return;
                    }
                }

                setDefaultUpcoming(tvUpcomingDate, tvUpcomingWorkoutName);
            }

            @Override
            public void onFailure(Call<AuthApi.WorkoutPlanResponse> call, Throwable t) {
                setDefaultUpcoming(tvUpcomingDate, tvUpcomingWorkoutName);
            }
        });
    }

    private void setDefaultUpcoming(TextView tvUpcomingDate, TextView tvUpcomingWorkoutName) {
        if (tvUpcomingDate != null) {
            Calendar next = Calendar.getInstance();
            next.add(Calendar.DAY_OF_YEAR, 1);
            String date = new SimpleDateFormat("EEEE, MMM d yyyy", Locale.getDefault()).format(next.getTime());
            tvUpcomingDate.setText(date);
        }
        if (tvUpcomingWorkoutName != null) {
            tvUpcomingWorkoutName.setText("No workout plan yet");
        }
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}