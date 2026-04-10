package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Stats extends BaseActivity {

    private GridLayout calendarGrid;
    private TextView tvMonthYear;
    private TextView tvPrev;
    private TextView tvNext;
    private TextView tvWorkoutsCompleted;
    private TextView tvCurrentStreak;
    private TextView tvLastWorkout;
    private TextView tvRecentWorkout1;
    private TextView tvRecentWorkout1Date;

    private Calendar displayedMonth;
    private final Set<String> workoutDayNames = new HashSet<>();
    private final Map<String, AuthApi.WorkoutPlanDay> workoutPlanByDay = new HashMap<>();

    private AuthApi.AuthService authService;

    private static final Map<Integer, String> DAY_NAMES = new HashMap<>();
    static {
        DAY_NAMES.put(Calendar.MONDAY, "monday");
        DAY_NAMES.put(Calendar.TUESDAY, "tuesday");
        DAY_NAMES.put(Calendar.WEDNESDAY, "wednesday");
        DAY_NAMES.put(Calendar.THURSDAY, "thursday");
        DAY_NAMES.put(Calendar.FRIDAY, "friday");
        DAY_NAMES.put(Calendar.SATURDAY, "saturday");
        DAY_NAMES.put(Calendar.SUNDAY, "sunday");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setActivityLayout(R.layout.activity_stats);

        if (findViewById(R.id.statsRoot) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statsRoot), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        authService = AuthApi.getService(this);

        calendarGrid = findViewById(R.id.calendarGrid);
        tvMonthYear = findViewById(R.id.tvCalendarMonthYear);
        tvPrev = findViewById(R.id.tvCalendarPrev);
        tvNext = findViewById(R.id.tvCalendarNext);
        tvWorkoutsCompleted = findViewById(R.id.tvWorkoutsCompleted);
        tvCurrentStreak = findViewById(R.id.tvCurrentStreak);
        tvLastWorkout = findViewById(R.id.tvLastWorkout);
        tvRecentWorkout1 = findViewById(R.id.tvRecentWorkout1);
        tvRecentWorkout1Date = findViewById(R.id.tvRecentWorkout1Date);

        displayedMonth = Calendar.getInstance();

        tvPrev.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, -1);
            buildCalendarGrid();
        });

        tvNext.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, 1);
            buildCalendarGrid();
        });

        MaterialButton btnEditSchedule = findViewById(R.id.btnEditSchedule);
        if (btnEditSchedule != null) {
            btnEditSchedule.setOnClickListener(v -> {
                Intent intent = new Intent(this, SchedulePreferencesActivity.class);
                intent.putExtra("fromEdit", true);
                startActivity(intent);
            });
        }

        loadStatsData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatsData();
    }

    private void loadStatsData() {
        loadProgress();
        loadWorkoutPlan();
    }

    private void loadProgress() {
        authService.getProgress().enqueue(new Callback<AuthApi.ProgressResponse>() {
            @Override
            public void onResponse(Call<AuthApi.ProgressResponse> call, Response<AuthApi.ProgressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthApi.ProgressResponse progress = response.body();

                    int total = progress.getTotalWorkouts() != null ? progress.getTotalWorkouts() : 0;
                    int streak = progress.getCurrentStreak() != null ? progress.getCurrentStreak() : 0;
                    String lastWorkout = progress.getLastWorkout() != null && !progress.getLastWorkout().isEmpty()
                            ? progress.getLastWorkout()
                            : "—";

                    if (tvWorkoutsCompleted != null) {
                        tvWorkoutsCompleted.setText(String.valueOf(total));
                    }

                    if (tvCurrentStreak != null) {
                        tvCurrentStreak.setText(streak + "🔥");
                    }

                    if (tvLastWorkout != null) {
                        tvLastWorkout.setText(lastWorkout);
                    }

                    if (tvRecentWorkout1 != null) {
                        if ("—".equals(lastWorkout)) {
                            tvRecentWorkout1.setText("No workouts yet");
                        } else {
                            tvRecentWorkout1.setText("Last completed workout");
                        }
                    }

                    if (tvRecentWorkout1Date != null) {
                        if ("—".equals(lastWorkout)) {
                            tvRecentWorkout1Date.setText("Complete your first workout");
                        } else {
                            tvRecentWorkout1Date.setText(lastWorkout);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthApi.ProgressResponse> call, Throwable t) {
                Toast.makeText(Stats.this, "Could not load progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWorkoutPlan() {
        authService.getWorkoutPlan().enqueue(new Callback<AuthApi.WorkoutPlanResponse>() {
            @Override
            public void onResponse(Call<AuthApi.WorkoutPlanResponse> call, Response<AuthApi.WorkoutPlanResponse> response) {
                workoutDayNames.clear();
                workoutPlanByDay.clear();

                if (response.isSuccessful() && response.body() != null && response.body().getPlan() != null) {
                    List<AuthApi.WorkoutPlanDay> plan = response.body().getPlan();

                    for (AuthApi.WorkoutPlanDay day : plan) {
                        if (day.getDay() != null) {
                            String normalizedDay = day.getDay().trim().toLowerCase();
                            workoutDayNames.add(normalizedDay);
                            workoutPlanByDay.put(normalizedDay, day);
                        }
                    }
                }

                buildCalendarGrid();
            }

            @Override
            public void onFailure(Call<AuthApi.WorkoutPlanResponse> call, Throwable t) {
                buildCalendarGrid();
            }
        });
    }

    private void buildCalendarGrid() {
        if (calendarGrid == null) return;

        calendarGrid.removeAllViews();

        if (tvMonthYear != null) {
            tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(displayedMonth.getTime()));
        }

        Calendar cal = (Calendar) displayedMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarGrid.addView(makeEmptyCell());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            Calendar dayCal = (Calendar) displayedMonth.clone();
            dayCal.set(Calendar.DAY_OF_MONTH, day);

            String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayCal.getTime());
            String dayName = DAY_NAMES.get(dayCal.get(Calendar.DAY_OF_WEEK));
            boolean isScheduled = workoutDayNames.contains(dayName);
            boolean isToday = dateStr.equals(todayStr);

            AuthApi.WorkoutPlanDay planDay = workoutPlanByDay.get(dayName);

            calendarGrid.addView(makeDayCell(day, dateStr, dayName, isScheduled, isToday, planDay));
        }
    }

    private View makeEmptyCell() {
        TextView tv = new TextView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(4, 4, 4, 4);
        tv.setLayoutParams(params);
        tv.setHeight(dpToPx(40));
        return tv;
    }

    private View makeDayCell(int day, String dateStr, String dayName, boolean isScheduled, boolean isToday, AuthApi.WorkoutPlanDay planDay) {
        TextView tv = new TextView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(4, 4, 4, 4);
        tv.setLayoutParams(params);
        tv.setHeight(dpToPx(40));
        tv.setGravity(Gravity.CENTER);
        tv.setText(String.valueOf(day));
        tv.setTextSize(13);

        if (isScheduled) {
            tv.setBackgroundResource(R.drawable.bg_cal_day_workout);
            tv.setTextColor(getColor(android.R.color.white));
        } else if (isToday) {
            tv.setBackgroundResource(R.drawable.bg_cal_day_today);
            tv.setTextColor(0xFF0078AE);
        } else {
            tv.setBackgroundResource(R.drawable.bg_cal_day_default);
            tv.setTextColor(0xFF1A1A2E);
        }

        if (isScheduled) {
            tv.setOnClickListener(v -> showDayDetail(dateStr, dayName, planDay));
        }

        return tv;
    }

    private void showDayDetail(String dateStr, String dayName, AuthApi.WorkoutPlanDay planDay) {
        StringBuilder msg = new StringBuilder();
        msg.append("Date: ").append(dateStr).append("\n\n");
        msg.append("Workout day: ").append(capitalize(dayName)).append("\n");

        if (planDay != null) {
            if (planDay.getWorkout() != null) {
                msg.append("Workout: ").append(planDay.getWorkout()).append("\n");
            }

            if (planDay.getDuration() != null) {
                msg.append("Duration: ").append(planDay.getDuration()).append(" mins\n");
            }

            if (planDay.getIntensity() != null) {
                msg.append("Difficulty: ").append(capitalize(planDay.getIntensity())).append("\n");
            }

            if (planDay.getExercises() != null && !planDay.getExercises().isEmpty()) {
                msg.append("\nExercises:\n");
                for (AuthApi.ExerciseItem ex : planDay.getExercises()) {
                    String line = "• " + ex.getExerciseName();
                    if (ex.getSets() != null && ex.getReps() != null) {
                        line += " , " + ex.getSets() + " sets x " + ex.getReps();
                    }
                    msg.append(line).append("\n");
                }
            }
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(capitalize(dayName) + " Workout")
                .setMessage(msg.toString().trim())
                .setPositiveButton("Close", null)
                .show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}