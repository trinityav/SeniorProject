package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

    // ── Water constants ───────────────────────────────────────────────────
    private static final int    WATER_GOAL_ML    = 2000;
    private static final int    WATER_STEP_ML    = 250;
    private static final String PREF_WATER       = "water_prefs";
    private static final String KEY_WATER        = "water_today_ml";
    private static final String KEY_WATER_DATE   = "water_date";

    // ── Existing fields ───────────────────────────────────────────────────
    private GridLayout calendarGrid;
    private TextView tvMonthYear;
    private TextView tvPrev;
    private TextView tvNext;
    private TextView tvWorkoutsCompleted;
    private TextView tvCurrentStreak;
    private TextView tvLastWorkout;
    private TextView tvRecentWorkout1;
    private TextView tvRecentWorkout1Date;

    // ── Water fields ──────────────────────────────────────────────────────
    private LinearLayout waterMeterFill;
    private TextView     tvWaterAmount;

    private Calendar displayedMonth;
    private final Set<String> workoutDayNames = new HashSet<>();
    private final Map<String, AuthApi.WorkoutPlanItem> workoutPlanByDay = new HashMap<>();

    // Tracks which dates the user has manually toggled complete this session
    private final Set<String> completedDates = new HashSet<>();

    private AuthApi.AuthService authService;

    private static final Map<Integer, String> DAY_NAMES = new HashMap<>();
    static {
        DAY_NAMES.put(Calendar.MONDAY,    "monday");
        DAY_NAMES.put(Calendar.TUESDAY,   "tuesday");
        DAY_NAMES.put(Calendar.WEDNESDAY, "wednesday");
        DAY_NAMES.put(Calendar.THURSDAY,  "thursday");
        DAY_NAMES.put(Calendar.FRIDAY,    "friday");
        DAY_NAMES.put(Calendar.SATURDAY,  "saturday");
        DAY_NAMES.put(Calendar.SUNDAY,    "sunday");
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

        // ── Existing view bindings ────────────────────────────────────────
        calendarGrid        = findViewById(R.id.calendarGrid);
        tvMonthYear         = findViewById(R.id.tvCalendarMonthYear);
        tvPrev              = findViewById(R.id.tvCalendarPrev);
        tvNext              = findViewById(R.id.tvCalendarNext);
        tvWorkoutsCompleted = findViewById(R.id.tvWorkoutsCompleted);
        tvCurrentStreak     = findViewById(R.id.tvCurrentStreak);
        tvLastWorkout       = findViewById(R.id.tvLastWorkout);
        tvRecentWorkout1    = findViewById(R.id.tvRecentWorkout1);
        tvRecentWorkout1Date = findViewById(R.id.tvRecentWorkout1Date);

        // ── Water view bindings ───────────────────────────────────────────
        waterMeterFill = findViewById(R.id.waterMeterFill);
        tvWaterAmount  = findViewById(R.id.tvWaterAmount);

        LinearLayout waterTapArea = findViewById(R.id.waterTapArea);
        if (waterTapArea != null) {
            waterTapArea.setOnClickListener(v -> addWater());
        }

        resetWaterIfNewDay();
        updateWaterUI();

        // ── Existing logic ────────────────────────────────────────────────
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

    // ── Existing methods (unchanged) ──────────────────────────────────────

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

                    int total  = progress.getTotalWorkouts()  != null ? progress.getTotalWorkouts()  : 0;
                    int streak = progress.getCurrentStreak()  != null ? progress.getCurrentStreak()  : 0;
                    String lastWorkout = progress.getLastWorkout() != null && !progress.getLastWorkout().isEmpty()
                            ? progress.getLastWorkout() : "—";

                    if (tvWorkoutsCompleted != null) tvWorkoutsCompleted.setText(String.valueOf(total));
                    if (tvCurrentStreak    != null) tvCurrentStreak.setText(streak + "🔥");
                    if (tvLastWorkout      != null) tvLastWorkout.setText(lastWorkout);

                    if (tvRecentWorkout1 != null) {
                        tvRecentWorkout1.setText("—".equals(lastWorkout) ? "No workouts yet" : "Last completed workout");
                    }
                    if (tvRecentWorkout1Date != null) {
                        tvRecentWorkout1Date.setText("—".equals(lastWorkout) ? "Complete your first workout" : lastWorkout);
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

                if (response.isSuccessful() && response.body() != null && response.body().getItems() != null) {
                    for (AuthApi.WorkoutPlanItem item : response.body().getItems()) {
                        if (item.getDay() != null) {
                            String normalizedDay = item.getDay().trim().toLowerCase();
                            workoutDayNames.add(normalizedDay);
                            workoutPlanByDay.put(normalizedDay, item);
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
            tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    .format(displayedMonth.getTime()));
        }

        Calendar cal = (Calendar) displayedMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth    = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String todayStr    = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarGrid.addView(makeEmptyCell());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            Calendar dayCal = (Calendar) displayedMonth.clone();
            dayCal.set(Calendar.DAY_OF_MONTH, day);

            String  dateStr     = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayCal.getTime());
            String  dayName     = DAY_NAMES.get(dayCal.get(Calendar.DAY_OF_WEEK));
            boolean isScheduled = workoutDayNames.contains(dayName);
            boolean isToday     = dateStr.equals(todayStr);
            AuthApi.WorkoutPlanItem planItem = workoutPlanByDay.get(dayName);

            calendarGrid.addView(makeDayCell(day, dateStr, dayName, isScheduled, isToday, planItem));
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

    private View makeDayCell(int day, String dateStr, String dayName,
                             boolean isScheduled, boolean isToday,
                             AuthApi.WorkoutPlanItem planItem) {
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

        // Apply style (completed overrides scheduled)
        applyDayCellStyle(tv, dateStr, isScheduled, isToday);

        if (isScheduled) {
            tv.setOnClickListener(v -> {
                // Toggle completed state
                if (completedDates.contains(dateStr)) {
                    completedDates.remove(dateStr);
                } else {
                    completedDates.add(dateStr);
                }
                applyDayCellStyle(tv, dateStr, true, isToday);

                // Still show the detail dialog on tap
                showDayDetail(dateStr, dayName, planItem);
            });
        }

        return tv;
    }

    // ── NEW: split style into its own method so toggle can re-call it ─────
    private void applyDayCellStyle(TextView tv, String dateStr,
                                   boolean isScheduled, boolean isToday) {
        if (completedDates.contains(dateStr)) {
            tv.setBackgroundResource(R.drawable.bg_cal_day_done);
            tv.setTextColor(Color.WHITE);
        } else if (isScheduled) {
            tv.setBackgroundResource(R.drawable.bg_cal_day_workout);
            tv.setTextColor(getColor(android.R.color.white));
        } else if (isToday) {
            tv.setBackgroundResource(R.drawable.bg_cal_day_today);
            tv.setTextColor(0xFF0078AE);
        } else {
            tv.setBackgroundResource(R.drawable.bg_cal_day_default);
            tv.setTextColor(0xFF1A1A2E);
        }
    }

    private void showDayDetail(String dateStr, String dayName, AuthApi.WorkoutPlanItem planItem) {
        StringBuilder msg = new StringBuilder();
        msg.append("Date: ").append(dateStr).append("\n\n");
        msg.append("Workout day: ").append(capitalize(dayName)).append("\n");

        if (planItem != null) {
            if (planItem.getFocus() != null)
                msg.append("Workout: ").append(planItem.getFocus()).append("\n");
            if (planItem.getEstimatedTotalMinutes() != null)
                msg.append("Duration: ").append(planItem.getEstimatedTotalMinutes()).append(" mins\n");
            if (planItem.getExercises() != null && !planItem.getExercises().isEmpty()) {
                msg.append("\nExercises:\n");
                for (AuthApi.ExerciseItem ex : planItem.getExercises()) {
                    String line = "• " + ex.getExerciseName();
                    if (ex.getSets() != null && ex.getReps() != null)
                        line += " , " + ex.getSets() + " sets x " + ex.getReps();
                    msg.append(line).append("\n");
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(capitalize(dayName) + " Workout")
                .setMessage(msg.toString().trim())
                .setPositiveButton("Close", null)
                .show();
    }

    // ── Water methods (new) ───────────────────────────────────────────────

    private void addWater() {
        SharedPreferences prefs = getSharedPreferences(PREF_WATER, MODE_PRIVATE);
        int current = prefs.getInt(KEY_WATER, 0);
        current += WATER_STEP_ML;

        if (current >= WATER_GOAL_ML) {
            prefs.edit().putInt(KEY_WATER, WATER_GOAL_ML).apply();
            updateWaterUI();
            new AlertDialog.Builder(this)
                    .setTitle("🎉 Goal Reached!")
                    .setMessage("You hit your " + WATER_GOAL_ML + "ml water goal for today!")
                    .setPositiveButton("Reset", (d, w) -> {
                        prefs.edit().putInt(KEY_WATER, 0).apply();
                        updateWaterUI();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            prefs.edit().putInt(KEY_WATER, current).apply();
            updateWaterUI();
        }
    }

    private void resetWaterIfNewDay() {
        SharedPreferences prefs = getSharedPreferences(PREF_WATER, MODE_PRIVATE);
        String today    = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        String lastDate = prefs.getString(KEY_WATER_DATE, "");
        if (!today.equals(lastDate)) {
            prefs.edit().putInt(KEY_WATER, 0).putString(KEY_WATER_DATE, today).apply();
        }
    }

    private void updateWaterUI() {
        if (waterMeterFill == null || tvWaterAmount == null) return;
        SharedPreferences prefs = getSharedPreferences(PREF_WATER, MODE_PRIVATE);
        int   current = prefs.getInt(KEY_WATER, 0);
        float pct     = Math.min((float) current / WATER_GOAL_ML, 1f);

        waterMeterFill.post(() -> {
            View parent = (View) waterMeterFill.getParent();
            if (parent == null) return;
            int padding   = dpToPx(8);
            int fillWidth = Math.round((parent.getWidth() - padding * 2) * pct);
            android.view.ViewGroup.LayoutParams lp = waterMeterFill.getLayoutParams();
            lp.width = Math.max(fillWidth, 0);
            waterMeterFill.setLayoutParams(lp);
        });

        tvWaterAmount.setText(current + " / " + WATER_GOAL_ML + " ml");
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}