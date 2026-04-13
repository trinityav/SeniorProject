package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private CardView cardAiCoach;
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

        cardAiCoach = findViewById(R.id.cardAiCoach);
        if (cardAiCoach != null) {
            cardAiCoach.setOnClickListener(v -> showAiCoachDialog());
        }

        CardView upcomingWorkoutCard = findViewById(R.id.upcomingWorkoutCard);
        if (upcomingWorkoutCard != null) {
            upcomingWorkoutCard.setOnClickListener(v ->
                    startActivity(new Intent(HomeScreen.this, WorkoutsActivity.class))
            );
        }
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
                if (tvTotalWorkouts != null) {
                    tvTotalWorkouts.setText("0");
                }
            }
        });
    }

    private void loadWorkoutPlanPreview(TextView tvUpcomingDate, TextView tvUpcomingWorkoutName) {
        authService.getWorkoutPlan().enqueue(new Callback<AuthApi.WorkoutPlanResponse>() {
            @Override
            public void onResponse(Call<AuthApi.WorkoutPlanResponse> call, Response<AuthApi.WorkoutPlanResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getItems() != null) {
                    List<AuthApi.WorkoutPlanItem> items = response.body().getItems();

                    if (!items.isEmpty()) {
                        AuthApi.WorkoutPlanItem first = items.get(0);

                        if (tvUpcomingDate != null && first.getDay() != null) {
                            tvUpcomingDate.setText(capitalize(first.getDay()));
                        }

                        if (tvUpcomingWorkoutName != null) {
                            String workoutName = first.getFocus() != null && !first.getFocus().isEmpty()
                                    ? first.getFocus()
                                    : "Workout";
                            tvUpcomingWorkoutName.setText(workoutName);
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

    private void showAiCoachDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ai_coach, null);
        builder.setView(dialogView);

        EditText etUserQuestion = dialogView.findViewById(R.id.etUserQuestion);
        TextView tvAiResponse = dialogView.findViewById(R.id.tvAiResponse);
        Button btnSend = dialogView.findViewById(R.id.btnSend);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
            );
        }

        btnSend.setOnClickListener(v -> {
            String userQuestion = etUserQuestion.getText().toString().trim();

            if (userQuestion.isEmpty()) {
                Toast.makeText(this, "Please type a question", Toast.LENGTH_SHORT).show();
                return;
            }

            tvAiResponse.setText("Thinking...");
            btnSend.setEnabled(false);

            AuthApi.ChatbotRequest request = new AuthApi.ChatbotRequest(userQuestion);

            authService.askChatbot(request).enqueue(new Callback<AuthApi.ChatbotResponse>() {
                @Override
                public void onResponse(Call<AuthApi.ChatbotResponse> call, Response<AuthApi.ChatbotResponse> response) {
                    btnSend.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        String answer = response.body().getAnswer();

                        if (answer != null) {
                            tvAiResponse.setText(android.text.Html.fromHtml(answer.replace("\n", "<br>")));
                        } else {
                            tvAiResponse.setText("No response received.");
                        }

                    } else if (response.code() == 401) {
                        tvAiResponse.setText("You are not logged in. Please sign in again.");
                    } else {
                        tvAiResponse.setText("Chatbot request failed.");
                    }
                }

                @Override
                public void onFailure(Call<AuthApi.ChatbotResponse> call, Throwable t) {
                    btnSend.setEnabled(true);
                    tvAiResponse.setText("Error: " + t.getMessage());
                }
            });
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}