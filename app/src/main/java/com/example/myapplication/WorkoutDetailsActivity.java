package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutDetailsActivity extends AppCompatActivity {

    private static final String KEY_DONE_PREFIX = "done_";

    private Workout workout;
    private AuthApi.AuthService authService;
    private String currentUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        authService = AuthApi.getService(this);

        SessionManager sessionManager = new SessionManager(this);
        currentUsername = sessionManager.getUsername();
        if (currentUsername == null || currentUsername.trim().isEmpty()) {
            currentUsername = "default_user";
        }

        workout = (Workout) getIntent().getSerializableExtra("workout_day");

        TextView tvTitle = findViewById(R.id.tvWorkoutDetailsTitle);
        TextView tvMeta = findViewById(R.id.tvWorkoutMeta);
        LinearLayout workoutCardsContainer = findViewById(R.id.workoutCardsContainer);
        MaterialButton btnBackToWorkouts = findViewById(R.id.btnBackToWorkouts);
        MaterialButton btnWorkoutDone = findViewById(R.id.btnWorkoutDone);

        btnBackToWorkouts.setOnClickListener(v -> finish());

        if (workout == null) {
            tvTitle.setText("Workout Details");
            tvMeta.setText("No workout selected");
            btnWorkoutDone.setEnabled(false);
            return;
        }

        tvTitle.setText(capitalize(workout.getDay()) + ", " + workout.getWorkoutName());
        tvMeta.setText("Duration, " + workout.getDuration() + " mins    Difficulty, " + capitalize(workout.getIntensity()));

        addExerciseCards(workoutCardsContainer, workout.getExercises());

        String workoutKey = getWorkoutCompletionKey();

        if (isWorkoutMarkedDone(workoutKey)) {
            btnWorkoutDone.setText("Workout Completed");
            btnWorkoutDone.setEnabled(false);
        }

        btnWorkoutDone.setOnClickListener(v -> handleWorkoutDoneClick(btnWorkoutDone, workoutKey));
    }

    private void addExerciseCards(LinearLayout container, List<String> exercises) {
        container.removeAllViews();

        if (exercises == null || exercises.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No exercises listed");
            emptyText.setTextSize(15f);
            emptyText.setTextColor(0xFF444444);
            container.addView(emptyText);
            return;
        }

        for (String exercise : exercises) {
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.bottomMargin = dpToPx(10);
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(dpToPx(14));
            cardView.setCardElevation(dpToPx(3));
            cardView.setUseCompatPadding(true);
            cardView.setCardBackgroundColor(0xFFF3F1F4);

            TextView tvExercise = new TextView(this);
            tvExercise.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tvExercise.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
            tvExercise.setText("• " + exercise);
            tvExercise.setTextSize(15f);
            tvExercise.setTextColor(0xFF222222);

            cardView.addView(tvExercise);
            container.addView(cardView);
        }
    }

    private void handleWorkoutDoneClick(MaterialButton btnWorkoutDone, String workoutKey) {
        String todayDayName = getTodayDayName();
        String selectedWorkoutDay = workout.getDay() != null ? workout.getDay().trim().toLowerCase() : "";

        if (!selectedWorkoutDay.equals(todayDayName)) {
            if (isTodayWorkoutCompleted()) {
                Toast.makeText(
                        this,
                        "Workout for today is already completed. Come back on this day to complete this one.",
                        Toast.LENGTH_LONG
                ).show();
            } else {
                Toast.makeText(
                        this,
                        "Complete today's workout and come back on this day to do this one.",
                        Toast.LENGTH_LONG
                ).show();
            }
            return;
        }

        markWorkoutDone(btnWorkoutDone, workoutKey);
    }

    private void markWorkoutDone(MaterialButton btnWorkoutDone, String workoutKey) {
        String performedDate = getTodayDate();

        saveWorkoutDoneKey(workoutKey);
        saveCompletedDate(performedDate);
        saveTodayWorkoutDayCompleted();

        try {
            AuthApi.WorkoutLogRequest request =
                    new AuthApi.WorkoutLogRequest(
                            performedDate,
                            workout.getWorkoutName(),
                            workout.getDuration(),
                            workout.getIntensity()
                    );

            authService.logWorkout(request).enqueue(new Callback<AuthApi.MessageResponse>() {
                @Override
                public void onResponse(Call<AuthApi.MessageResponse> call, Response<AuthApi.MessageResponse> response) {
                    btnWorkoutDone.setText("Workout Completed");
                    btnWorkoutDone.setEnabled(false);
                    Toast.makeText(WorkoutDetailsActivity.this, "Workout marked as done", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<AuthApi.MessageResponse> call, Throwable t) {
                    btnWorkoutDone.setText("Workout Completed");
                    btnWorkoutDone.setEnabled(false);
                    Toast.makeText(WorkoutDetailsActivity.this, "Workout marked as done", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            btnWorkoutDone.setText("Workout Completed");
            btnWorkoutDone.setEnabled(false);
            Toast.makeText(this, "Workout marked as done", Toast.LENGTH_SHORT).show();
        }
    }

    private String getWorkoutCompletionKey() {
        String weekKey = getCurrentWeekKey();
        String day = workout.getDay() != null ? workout.getDay().toLowerCase() : "unknown";
        String name = workout.getWorkoutName() != null ? workout.getWorkoutName().toLowerCase().replace(" ", "_") : "workout";
        return weekKey + "_" + day + "_" + name;
    }

    private String getCurrentWeekKey() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(calendar.getTime());
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private String getTodayDayName() {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date()).toLowerCase();
    }

    private String getWorkoutPrefsName() {
        return "workout_done_prefs_" + currentUsername;
    }

    private void saveWorkoutDoneKey(String workoutKey) {
        SharedPreferences prefs = getSharedPreferences(getWorkoutPrefsName(), MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_DONE_PREFIX + workoutKey, true).apply();
    }

    private void saveCompletedDate(String date) {
        SharedPreferences prefs = getSharedPreferences(getWorkoutPrefsName(), MODE_PRIVATE);
        prefs.edit().putBoolean("done_date_" + date, true).apply();
    }

    private boolean isWorkoutMarkedDone(String workoutKey) {
        SharedPreferences prefs = getSharedPreferences(getWorkoutPrefsName(), MODE_PRIVATE);
        return prefs.getBoolean(KEY_DONE_PREFIX + workoutKey, false);
    }

    private void saveTodayWorkoutDayCompleted() {
        SharedPreferences prefs = getSharedPreferences(getWorkoutPrefsName(), MODE_PRIVATE);
        String todayWeekKey = getCurrentWeekKey();
        String todayDayName = getTodayDayName();

        prefs.edit()
                .putBoolean("done_today_" + todayWeekKey + "_" + todayDayName, true)
                .apply();
    }

    private boolean isTodayWorkoutCompleted() {
        SharedPreferences prefs = getSharedPreferences(getWorkoutPrefsName(), MODE_PRIVATE);
        String todayWeekKey = getCurrentWeekKey();
        String todayDayName = getTodayDayName();

        return prefs.getBoolean("done_today_" + todayWeekKey + "_" + todayDayName, false);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}