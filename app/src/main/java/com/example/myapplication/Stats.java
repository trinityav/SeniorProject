package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.ScheduleEntry;
import com.example.myapplication.data.local.ScheduleEntryDao;
import com.example.myapplication.data.local.WorkoutDayDao;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

public class Stats extends BaseActivity {

    private GridLayout calendarGrid;
    private TextView tvMonthYear, tvPrev, tvNext, tvWorkoutsCompleted, tvLastWorkout;
    private Calendar displayedMonth;
    private Map<String, WorkoutDay> workoutDayMap = new HashMap<>();
    private Set<String> scheduledDayNames = new HashSet<>(); // e.g. "Monday", "Tuesday"
    private WorkoutDayDao workoutDayDao;
    private ScheduleEntryDao scheduleEntryDao;

    // Maps day-of-week int (Calendar.MONDAY etc.) to name
    private static final Map<Integer, String> DAY_NAMES = new HashMap<>();
    static {
        DAY_NAMES.put(Calendar.MONDAY,    "Monday");
        DAY_NAMES.put(Calendar.TUESDAY,   "Tuesday");
        DAY_NAMES.put(Calendar.WEDNESDAY, "Wednesday");
        DAY_NAMES.put(Calendar.THURSDAY,  "Thursday");
        DAY_NAMES.put(Calendar.FRIDAY,    "Friday");
        DAY_NAMES.put(Calendar.SATURDAY,  "Saturday");
        DAY_NAMES.put(Calendar.SUNDAY,    "Sunday");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setActivityLayout(R.layout.activity_stats);

        if (findViewById(R.id.statsRoot) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statsRoot),
                    (v, insets) -> {
                        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        v.setPadding(systemBars.left, systemBars.top,
                                systemBars.right, systemBars.bottom);
                        return insets;
                    });
        }

        AppDatabase db  = AppDatabase.getInstance(this);
        workoutDayDao   = db.workoutDayDao();
        scheduleEntryDao = db.scheduleEntryDao();

        calendarGrid        = findViewById(R.id.calendarGrid);
        tvMonthYear         = findViewById(R.id.tvCalendarMonthYear);
        tvPrev              = findViewById(R.id.tvCalendarPrev);
        tvNext              = findViewById(R.id.tvCalendarNext);
        tvWorkoutsCompleted = findViewById(R.id.tvWorkoutsCompleted);
        tvLastWorkout       = findViewById(R.id.tvLastWorkout);

        displayedMonth = Calendar.getInstance();

        tvPrev.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, -1);
            loadAll();
        });

        tvNext.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, 1);
            loadAll();
        });

        MaterialButton btnEditSchedule = findViewById(R.id.btnEditSchedule);
        if (btnEditSchedule != null) {
            btnEditSchedule.setOnClickListener(v -> {
                Intent intent = new Intent(this, SchedulePreferencesActivity.class);
                intent.putExtra("fromEdit", true);
                startActivity(intent);
            });
        }

        loadAll();
        loadTopStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh calendar when returning from EditSchedule
        loadAll();
    }

    private void loadAll() {
        String yearMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                .format(displayedMonth.getTime());

        Executors.newSingleThreadExecutor().execute(() -> {
            // Load schedule (which day names are workout days)
            List<String> days = scheduleEntryDao.getScheduledDays();
            scheduledDayNames.clear();
            scheduledDayNames.addAll(days);

            // Load any completed/logged workout days for this month
            List<WorkoutDay> logged = workoutDayDao.getDaysForMonth(yearMonth);
            workoutDayMap.clear();
            for (WorkoutDay wd : logged) workoutDayMap.put(wd.date, wd);

            runOnUiThread(() -> {
                tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        .format(displayedMonth.getTime()));
                buildCalendarGrid();
            });
        });
    }

    private void buildCalendarGrid() {
        calendarGrid.removeAllViews();

        Calendar cal = (Calendar) displayedMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        int daysInMonth    = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String todayStr    = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarGrid.addView(makeEmptyCell());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            String dateStr = new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                    .format(displayedMonth.getTime())
                    + "-" + String.format(Locale.getDefault(), "%02d", day);

            // Figure out what day of week this date is
            Calendar dayCal = (Calendar) displayedMonth.clone();
            dayCal.set(Calendar.DAY_OF_MONTH, day);
            String dayName = DAY_NAMES.get(dayCal.get(Calendar.DAY_OF_WEEK));
            boolean isScheduled = scheduledDayNames.contains(dayName);

            WorkoutDay wd  = workoutDayMap.get(dateStr);
            boolean isToday = dateStr.equals(todayStr);

            calendarGrid.addView(makeDayCell(day, dateStr, wd, isToday, isScheduled));
        }
    }

    private View makeEmptyCell() {
        TextView tv = new TextView(this);
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(4, 4, 4, 4);
        tv.setLayoutParams(p);
        tv.setHeight(dpToPx(40));
        return tv;
    }

    private View makeDayCell(int day, String dateStr, WorkoutDay wd,
                             boolean isToday, boolean isScheduled) {
        TextView tv = new TextView(this);
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(4, 4, 4, 4);
        tv.setLayoutParams(p);
        tv.setHeight(dpToPx(40));
        tv.setGravity(Gravity.CENTER);
        tv.setText(String.valueOf(day));
        tv.setTextSize(13);

        if (wd != null && wd.completed) {
            // Green = workout logged as completed
            tv.setBackgroundResource(R.drawable.bg_cal_day_done);
            tv.setTextColor(Color.WHITE);
        } else if (isScheduled) {
            // Blue = scheduled workout day from signup
            tv.setBackgroundResource(R.drawable.bg_cal_day_workout);
            tv.setTextColor(Color.WHITE);
        } else if (isToday) {
            // Outlined = today, no workout
            tv.setBackgroundResource(R.drawable.bg_cal_day_today);
            tv.setTextColor(Color.parseColor("#0078AE"));
        } else {
            tv.setBackgroundResource(R.drawable.bg_cal_day_default);
            tv.setTextColor(Color.parseColor("#1A1A2E"));
        }

        // Only scheduled days and completed days are tappable
        if (isScheduled || (wd != null && wd.completed)) {
            tv.setOnClickListener(v -> showDayDetail(dateStr, wd, isScheduled));
        }

        return tv;
    }

    private void showDayDetail(String dateStr, WorkoutDay wd, boolean isScheduled) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Get schedule info for that day's time window
            Calendar dayCal = Calendar.getInstance();
            try {
                dayCal.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr));
            } catch (Exception ignored) {}

            String dayName = DAY_NAMES.get(dayCal.get(Calendar.DAY_OF_WEEK));
            ScheduleEntry entry = scheduleEntryDao.getByDay(dayName != null ? dayName : "");

            runOnUiThread(() -> {
                StringBuilder msg = new StringBuilder();
                msg.append("📅  ").append(dateStr).append("\n\n");

                if (entry != null) {
                    msg.append("🕐  ").append(entry.startTime)
                            .append(" – ").append(entry.endTime).append("\n\n");
                }

                if (wd != null && wd.workoutName != null && !wd.workoutName.isEmpty()) {
                    msg.append("🏋️  ").append(wd.workoutName).append("\n");
                } else {
                    msg.append("🏋️  Workout scheduled\n");
                }

                if (wd != null && wd.notes != null && !wd.notes.isEmpty()) {
                    msg.append("\n📝  ").append(wd.notes);
                }

                if (wd != null && wd.completed) {
                    msg.append("\n\n✅  Completed");
                }

                new AlertDialog.Builder(this)
                        .setTitle(dayName + " Workout")
                        .setMessage(msg.toString())
                        .setPositiveButton("Close", null)
                        .show();
            });
        });
    }

    private void loadTopStats() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int total = workoutDayDao.getTotalCompleted();
            List<WorkoutDay> recent = workoutDayDao.getRecentWorkouts();
            String lastDate = recent.isEmpty() ? "—" : recent.get(0).date.substring(5);

            runOnUiThread(() -> {
                tvWorkoutsCompleted.setText(String.valueOf(total));
                tvLastWorkout.setText(lastDate);
            });
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
